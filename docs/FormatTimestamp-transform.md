# Format Timestamp Transform

Description
-----------
Transforms the timestamp into a human readable string.

Configuration
-------------
**TimestampField:** Specifies the name of the field that contains the timestamp. Defaults to 'ts'.

**DatePattern:** Specifies the date pattern to use when formatting the timestamp. Defaults to 'yyyy-MM-dd'T'HH:mm:ss.SSS
[More Information](http://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html)

**DateField:** Specifies the name of the field to add containing the formatted timestamp. Defaults to 'date'.

**DropTimestamp:** Specifies whether the original timestamp field should be dropped. Defaults to true.
