#! /bin/bash

# Get java time
javac GetTime.java
time="$(java GetTime)"
rm GetTime.class

# Sql Queries
removeOldSql="DELETE FROM Events WHERE start_time < ${time};"
removeOrphanedSql="DELETE FROM Attending WHERE eid NOT IN (SELECT e.eid FROM Events e);"

# Update the file that contains the sql
echo $removeOldSql > GCQueries.sql # this deletes contents of file
echo "" >> GCQueries.sql
echo $removeOrphanedSql >> GCQueries.sql

# Execute the GC queries
mysql -h matchonthestreetdb.crqizzvrxges.us-east-1.rds.amazonaws.com -P 3306 -u larioj motsdb -pmotspassword < GCQueries.sql

## Delete tmp files
rm GCQueries.sql
