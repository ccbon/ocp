#!/bin/usr/ksh
set -eau

FILENAME="${1}"
PATHNAME="${2}"

split_file "${FILENAME}" "${PATHNAME}"
