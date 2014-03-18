MAS
===

Updates:
----
I added Beacons, which can be used by an agent to notify other agents of what it is doing or what it expects others to be doing. Beacons have the following properties:
  * Sender ID: the ID of the agent that originally placed the beacon.
  * Type     : the type (e.g. MOVE, HELP_SAVE).
  * Location : where the beacon is.
  * Agents   : the number of agents required.
  * Round    : the time that the beacon is relevent (e.g. with type MOVE, the agent will be at the location at time Round, or if TYPE is HELP_SAVE, the agent will be trying to save the survivor at time Round.)

The Simulation object will now store where agents expect to be at certain times. If there is no explicit entry in this database at some time t, it will take the location at the latest time less than t.

-_Nathan_


----
I added a basic intelligence to the SAP package, and filled in the Action class. The SAP intelligence is pretty close to functioning I think, the goal is that it should be really easy to insert new pairs to define the behaviors.

Also, I decided it would probably be a good idea to put synopses of changes here on the readme so that they show up right away when we visit the repository. I will be putting the most recent entries in this log at the top.

-_Nathan_

----
I added the package SAP, which includes some code that can be extended to implement Situation-Action pairs. It's quite simple at the moment, and I wasn't sure exactly which properties to use as dimensions so I picked a couple that seemed like obvious choices; however, I wasn't sure exactly how to normalize the properties so the actual distance dimensions may have a disproportionately small influence.

Additionally, I wasn't sure how we want to implement the Action part of the pairs (e.g. do we want it to use a common implementation between all intelligences, like the Goal queue we talked about earlier?) so I left that blank too.

-_Nathan_

----
