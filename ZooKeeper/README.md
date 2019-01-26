## < Distributed System Dynamic Node Manager >




                                                                    				    By Jason/GeW


*  Distributed System Dynamic Node Manager (server mode & client mode) based on Apache ZooKeeper.

*  Server mode (NodeManager) provides node registration alone with information, supports continuous watching
   coordinating with other nodes.

*  Client mode (NodeClient) provides sync method and async mechanism to get node(s) info under certain path.

*  Provide Session Watcher for re-initializing the client when the old one expired.

*  Advanced Watcher support signal/event output to the message queue for external process.

*  Provide usage example in Main.class, can generate standalone executable jar and support cli argument options.

*  This project is under Continuous Integration process. 
