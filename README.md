# simple consensus protocol implementation

based on documentation on [http://thesecretlivesofdata.com/raft/](http://thesecretlivesofdata.com/raft/)

to run peer execute jar with parameters : `--id=<id> --peers=<local ip:port on socket listening>,<peer list coma separated with struct ip:port>`

run with Makefile :

on separated cli : `make 0` `make 1` `make 2`
