#!/bin/bash -me

DBUSERNAME=admin
DBPASSWORD=password
HOST=${DOCKERHOST:-localhost}

# inspired from https://forums.couchbase.com/t/how-to-create-preconfigured-couchbase-docker-image-with-data/17004

# Check if couchbase server is up
check_db() {
  curl --silent http://${HOST}:8091/pools >/dev/null
  echo $?
}

log() {
  echo "[$(date +"%T")] $@"
}

stop() {
  docker rm -f local_couchbase >/dev/null || true
  log "Couchbase server is stopped"
}

run_statement() {
  curl -u $DBUSERNAME:$DBPASSWORD http://$HOST:8093/query/service \
    -d "statement=$1" >/dev/null
}

start() {

  docker rm -f local_couchbase >/dev/null || true

  # Run the server and send it to the background
  log "Starting couchbase ........."
  docker run --name local_couchbase --rm -d -p 8091-8094:8091-8094 -p 11210:11210 couchbase/server:community-6.0.0 >/dev/null

  # Wait until it's ready (or 60sec)
  THRESHOLD=30
  until [[ $(check_db) == 0 ]]; do
    log >&2 "Waiting for Couchbase Server ${HOST}:8091 to be available ..."
    sleep 2
    THRESHOLD=$((THRESHOLD - 1))
    if [ $THRESHOLD -eq 0 ]; then
      log "Timeout reached for starting couchbase. Logs:"
      docker exec local_couchbase cat /opt/couchbase/var/lib/couchbase/logs/info.log
      stop
      exit 1
    fi
  done

  # Setup node
  log "Setup nodes ........."
  curl -u $DBUSERNAME:$DBPASSWORD \
    -X POST http://$HOST:8091/nodes/self/controller/settings \
    -d path=/opt/couchbase/var/lib/couchbase/data -d index_path=/opt/couchbase/var/lib/couchbase/data >/dev/null

  # Setup index mode
  log "Setup index mode ........."
  curl -u $DBUSERNAME:$DBPASSWORD -X POST http://$HOST:8091/settings/indexes \
    -d 'storageMode=forestdb' \
    -H "Content-Type: application/x-www-form-urlencoded" >/dev/null

  curl -s -u $DBUSERNAME:$DBPASSWORD \
    -X POST http://$HOST:8091/pools/default \
    -d 'clusterName=portal&memoryQuota=512&indexMemoryQuota=512&ftsMemoryQuota=256' \
    -H "Content-Type: application/x-www-form-urlencoded" >/dev/null

  # Setup Services
  log "Setup services ........."
  curl -u $DBUSERNAME:$DBPASSWORD \
    -X POST http://$HOST:8091/node/controller/setupServices \
    -d "services=kv,index,n1ql,fts" >/dev/null

  # Setup admin UI name and password
  log "Setup admin UI credentials ........."
  curl -u $DBUSERNAME:$DBPASSWORD -X POST http://$HOST:8091/settings/web \
    -d "password=$DBPASSWORD&username=$DBUSERNAME&port=SAME" >/dev/null

  # Setup buckets

  log "Create library bucket ........."
  curl -u $DBUSERNAME:$DBPASSWORD -X POST http://$HOST:8091/pools/default/buckets \
    -d 'flushEnabled=1&threadsNumber=3& \
      evictionPolicy=valueOnly&ramQuotaMB=200&bucketType=couchbase&name=library' >/dev/null

  sleep 5
  log "Creating indices ........."

  # Common indices
  run_statement "CREATE INDEX meta_type_index ON library(_meta.type)"

  sleep 1

  log "Couchbase is now running couchbase on $HOST"
}

case "$1" in
start)
  start
  exit 0
  ;;
stop)
  stop
  exit 0
  ;;
*)
  echo "usage: ./couchbase_local.sh start/stop"
  ;;
esac