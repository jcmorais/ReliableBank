# ReliableBank 
@Universidade do Minho, Reliable Distributed Systems

## Synopsis

The work consists of the implementation in Java using a communication protocol group (jgcs) and a pair client/server tolerant to faults.

## Features

- Creation of a new bank account, returning its unique identifier.
- Launch a value (positive or negative) in an account. A negative value (Survey) is accepted only if there is sufficient balance in the account.
- Transfer between accounts. It should only be accepted if there is sufficient balance on account origin.
- List the last n moves an account.
- Persistent storage of data on servers in an embedded database (derby).
- State incremental transfer during recovery from a server that was temporarily out of service.
- Use of a replication protocol (Active Replication).
