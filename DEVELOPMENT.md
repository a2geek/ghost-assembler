# Development Notes

## Maven 

1. Use the supplied version of Maven. Not what is in the environment. Maven and associated plugins can be very delicate
with respect to the version of Maven and how the POM is configured. (For instance, use `./mvnw clean package` to build
the assembler.)
2. Since Maven no longer supports making the version dynamic, to bump versions, use the versions plugin:
   ```shell
   ./mvnw versions:set -DnewVersion=0.10.0-SNAPSHOT
   ./mvnw versions:commit
   ```
