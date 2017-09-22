# gr-person-hw
This project is a homework assignment given by Guaranteed Rate that focuses on reading data from files, displaying that data in different formats, and providing a REST API to access that data.

## How to Run
There are two different ways to run the program for step one. Go to the top level directory of the project and enter either:

1. Display the data from the three files in the ingest-files folder:
```
lein run -F
```

2. Display data from certain (or all) files in ingest-files by specifying them:
```
lein run -f "person_comma_delim.csv" -f "person_pipe_delim.csv" -f "person_space_delim.csv"
```

The output from these commands will display on the console. All output options will be displayed in a table format with each table labeled.

## Accessing the Data with the REST API

The data can also be accessed and added to through the REST API. There are three GET routes to access the data, or one POST route to add to the data. The string passed into the POST route can be any of the three delimited types (comma, pipe, space) that appear in the files.

To get the server running, go to the top level directory of the project and enter:
```
lein ring server-headless
```

The server will start on localhost port 3000. Once the server is running, you should be able to use any of the provided GET or POST routes. The GET calls will return a collection of pretty-printed JSON data (to console and/or webpage), and the POST call will return a pretty-printed JSON version of the newly added record.

Available URIs:

* localhost:3000/records - **PUT**
* localhost:3000/records/gender - **GET** records sorted by gender
* localhost:3000/records/birthdate - **GET** records sorted by date of birth
* localhost:3000/records/name - **GET** records sorted by (last) name

## Help

If you would like the available command line options for the project to appear, you can run the following:
```
lein run gr-person-hw --help
```
