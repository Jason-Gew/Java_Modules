## < GPS DataWarehouse >


                                                                    				    By Jason/GeW
                                      
                                                                    				    
*  Data Warehouse for GPS information per `Device User` and `Device Config` (one user can have multiple devices).

*  User and Device can be enable/disable, activate/deactivate separately via REST API.

*  Implemented User **ApiKey** generation and verification mechanism, enhanced CURD multi-factor authentication.

*  Reserved token for further identity and operation verification.

*  Provide Pagination (QPageRequest) and Sorting (QSort) (___timestamp___ _desc/asc_) 
   when fetching GPS data records by User and DeviceID.

*  Provide batch insertion and deletion for GPS data records.

*  Support GPS data query based on datetime range [(start: 2018-10-18, end: 2019-01-01), (start: 1539129600, end: 1546300800)].

*  Support _csv_, _json_, _xml_ GPS data file (with correct header/column name line) uploading, reading and insertion.

*  Will support streaming data integration.




`This project is under continous integration process!`




