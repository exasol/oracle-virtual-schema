#!/bin/bash
set -o errexit
set -o nounset
set -o pipefail

project_dir="$( cd "$(dirname "$0")/" >/dev/null 2>&1 ; pwd -P )"
readonly project_dir

target_dir="$project_dir/target"
readonly target_dir

log_dir="$target_dir/db-logs"
readonly log_dir

log_archive="$target_dir/db-logs.tar.bz2"
readonly log_archive

mkdir -p "$log_dir"

echo "All running containers:"
docker ps
container_id=$(docker ps --format '{{.ID}} {{.Image}}' | grep docker-db | awk '{print $1}')
readonly container_id
echo "Collecting logs from container ID $container_id to $log_dir"

docker cp "$container_id:/exa/logs/" "$log_dir"

echo "Creating archive $log_archive..."
cd "$target_dir"
tar -cjvf "$log_archive" "db-logs"

echo "Created log archive:"
ls -lha "$log_archive"

if [ -z "${GITHUB_OUTPUT:-}" ]; then
    GITHUB_OUTPUT="/dev/stdout"
fi
echo "db_log_archive=$log_archive" >> "$GITHUB_OUTPUT"
