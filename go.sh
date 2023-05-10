args=""
if [ $# -gt 0 ]; then
    args="?actor=$1"
fi
open "http://localhost:8080/quote$args"