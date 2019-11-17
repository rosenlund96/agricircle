# agricircle

To make the project run, first move the graphQL files, in order for Apollo Android to use these. 

In the project structure, you would see to packages:
-Com.example.agricircle.project
-GRAPHQL.graphql.com.example.agricircle.activities

Move the contents inside the second package, outside the java directory as follows:

1.Main
- graphql
  - com
    - example
      - agricircle
        - activities
          - ALL THE GRAPHQL FILES GO HERE
            
 2.JAVA
 - com
  - example
    - agricircle
      - project
        - ALL THE PROJECT FILES GO HERE
 3.RES
