#!/usr/bin/env bash

# Script for developer purposes, generating SQL for creating a new user that can be authenticated
# with the password of literally "password". (Therefore: only for development/test setup.)
#
# Usage:
#
#   add_user.sh <username>
#
# Examples:
#
#   add_user.sh localtestuser | sqlite3 playtakdb/players.db
#
#   for x in \$( seq 1 100 ) | do add_user.sh user$x | sqlite3 playtakdb/players.db
#
# Notes:
#
# 1. Only for use development purposes where the exact initial password doesn't matter!
#
# 2. Must start/restart server after new users have been added, since the server won't
#    be aware of manually created users.
#
# 3. Why this script is useful: email isn't usually configured locally, so you cannot get the randomly created password.
#    Also, this manual process would be laborious for a larger amount of test users or setting up a test bed.

if [ -z $1 ]; then
  echo "Error: Must provide username as first argument" 1>&2
  echo "See script source for usage notes." 1>&2
  exit 1
fi

# password hash = hash that authenticates to "password"
# email is empty string
sql="INSERT INTO players VALUES(1,'$1','\$2a\$10\$JpGWa00gDsDtJK0Ttq/dD.F2zTj9kCkdcHwO5pIZFfWbT3CfDY/C6','',1000.0,750.0,0,1000.0,0.0,0,0,0,'{}',0,0,0,0);"
echo $sql
