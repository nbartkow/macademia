# Introduction #

A description of the graph building algorithms and information on what will be stored in the relevant Mongo db collections.


# Mongo db collections #

Collections
> interests
> > ids=> 	similar:an array of interest ids

> users
> > ids=> 	interests:an array of interest ids
> > > institution:id


> collaboratorRequests
> > ids=>	interests:an array of interest ids
> > > institution:id

# Person/CollaboratorRequest centered graphs #
The method calculate person neighbors takes a person/collaboratorRequest and institution set as arguments
First it finds that persons id and uses it to look up in the users collection the interest that person has.

Second it begins to loop over the interest.
With the current interest it looks up in the users and collaboratorRequests that have that interest id and are in the institution set edges are then made between these and the interests

Next it uses the current interest id to look up the similar interest in the interests collection and begins to loop over these. As previously the similar interest ids are used to look up people and collaboratorRequests and add edges to them.

End the loops construct the graph object and return

# Interest centered graphs #
The method takes an interest and an institution set as arguments.
First it finds the interest's id and uses that id to look up similar interest in the interests collection.

It then begins to loop over these interests and looks up people and collaborator requests using the users and collaboratorRequests collections.

If there can be an edge made between the similar interest and a person or collaborator request in the institution set
it creates the edges between the similar interest and the central interest as well as the relevant people or collaborator requests.
Otherwise there is no edge between the interest and the similar interest and the similar interest will not be displayed.

End the loop.

Build the graph and return it.