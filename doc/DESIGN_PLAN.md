VOOGASalad Design Plan
==

## Introduction

The goal of this project is to make a game authoring environment for tower defense games. Tower defense games involve the user utilizing a variety of resources to defend objects called towers (sometimes just “territories”) which are usually attacked by non-playable characters. Since some tower defense games flip around the formula (that is, have the player attack a tower) and even allow for multiplayer, wherein one player might attack the tower while the other defends, we will design our authoring environment to be flexible to all of these varieties. Many consider tower defense to be a subgenre of real-time strategy games since the placement of defense resources is typically simultaneous with the attacking of the tower. However, many newer tower defense games have been more turn-oriented, so we will make our environment flexible to this choice.  In short, the environment should be extremely flexible to any type of tower defense game the user could want to create; in doing so, we want our design to also be flexible to the addition of other genre options in the future. 

From a high level, the authoring environment will function by presenting the author with a game area flanked by both a panel (or panels) containing objects which can be dragged and dropped into the game area and a panel (or panels) which will allow the user to specify the properties of objects they have placed. Objects will have various properties, including whether or not it is stackable, its health, its “team” (whether it works with the player or against them), its size, and its image representation options (color, hue, etc.). Since we want to implement shared editing, we will have a “host” editor who approves all change requests submitted by “guest” editors; the host will communicate any approved changes to guests. The view in this case will make calls to the model to update its state (for example, add a new tower); for the moment, we anticipate no model-to-view calls—the view will update itself based on user interaction and tell the model to update accordingly. There will be an option to regularly save the game that’s being built. We will have game logic options in the editor as well, including but not limited to:
 * The ability to specify when the game is over (win or loss)
 * Agent pathfinding logic 
 * Real-time or turn-based 
 * Defending or attacking
 * Single or multiplayer 

There will also be some functionality for the author to launch the game as it exists at the moment, test gameplay in the player, then close the window and return to editing.  

The authored games will be packaged up into executable files (with all the engine and player code needed to run the game) once the author decides to export. This file can then be executed and run by the Player, which deals with the user’s interactions with the game during gameplay. The player will have features expected of a standard game: pause menu, heads-up display, saving and loading, etc. The player will need to support the addition of objects to the map; this will be similar to when the actual game is being built, except that there will be limitations on the type and quantity of resources available. 


## Overview

The two major levels of our design are the authoring environment and the game engine. While there needs to be some sort of data handler to account for how to store our information and a player segment to load and initialize a game state, these two components largely act as a bridge between the main two components and are to some extent handled within them, as will be described in this section. 

Beginning with the authoring environment, this segment of our project will feature two key modules. First, there will be a Game Area module. This section of the authoring environment is designed to keep track of the actual layout of the data objects that a user would be manipulating. This is a somewhat isolated module in the sense that it largely does not interact with any components of the API beyond communicating with the objects that are being placed into this area. Even when interacting with these objects, the placement of an object into the game area has the main interaction with the rest of the program of setting some coordinate information of the placed object to be stored within the object’s properties. Though the bounds and relative position are important information, they will all be stored within the properties files that are being saved and loaded through the soon-to-be-discussed IO module. Note that this particular components is distinct from the actual play area that a player will see when running the game.

Next, the remainder of the actual authoring environment is set up within an Editor module. This is the part of the code that actually creates objects to store player-designated settings such as attack radius or tower type. The windows that the user would interact with to drag and drop components into the Game Area are listed here, as is some window of currently placed objects that a user could click on to modify past properties. These are the objects that actually interface with the EditController API and call to add or update placed objects. 

The final module of relevance in designing the authoring environment is the Sprites/Object module. This is the object that will actually hold and eventually transfer the user-defined information about how the object should operate during the game. Objects will be tried to be generalized as much as possible so that an obstacle can be somewhat identical in design to a tower that has no motion and no ability to shoot a projectile. Our program will also eventually draw a distinction between projectiles and standard placeable objects to reflect the unique behavior of animation and cross-cell movement (as well as eventually more complex behavior, such as bullet tracking). While the properties of these Sprites are created and defined in the authoring environment, they also are a component of the engine as well, as the engine will load them up and cause them to operate according to some defined behavior.

Moving to the connection between the authoring environment and the engine, the IO module will act as the saving and loading service that exists in a slightly different form for either one. From the authoring end of the program, the IO module functions through the AuthoringIOController API that either saves a state and flushes the information to a json file to be accessed by the game engine or loads in both the high level game settings as well as the actual game elements from a previously authored game. This module must be able to interact with our standardized data formatting as described within another part of the plan. This component must also be able to interact with both the Editing and Game Area modules to actually initialize the objects into the view panes and the Game Area’s layout. This will probably be done using calls to the EditController’s methods that already draw to the map, but it must also be sure to load in user-defined object types into the drag-and-drop object window. From the perspective of the engine, the IO module must also account for loading and drawing the elements into the game window once a game is selected. The module must also load in all possible games. Finally, some IO components must exist between these two segments to actually manage the files that are being passed into or saved from each end. While all of these parts comprise the IO functionality, the IO module will likely sit in an external package that functions between the two while both the authoring environment and engine have their own IO controller, as can currently be seen in the API.

On the engine side of things, the engine is comprised of two key modules. First, the Play Window module must account for loading in the level design specified by the data files and handling some heads up display. This will need to work with the IO controller and will function similarly to the Game Area of the authoring environment, with the added need to track some sort of time step to account for the main game loop under the engine’s code. The other module that works both in conjunction with the loaded Sprites/Objects and with the logic of updating the Play Window itself is the Behavior module. This module is designed to control the logic of each Object in terms of how actions like shooting a projectile or moving an enemy soldier are calculated. This is largely where the flexibility in the system resides, as it changes the more dynamic rules of the game during the actual game loop. It also accounts for more interesting components such as AI behavior. This module will rely on the Behavior interface in the API and allows the code to override important functionality such as how the object should update on each iteration of the game loop (`update()`), returning the status of the object or the world (`isWon()`, `getStatus()`), and in choosing what to do when the object has a state change (checking for changes with `getLives()`), `pause()`).


## User Interface

When the user starts the program, an opening window will appear that will allow the user to choose to create a new game, load and edit an existing game, or play an existing game. 

![Open Screen](doc/Vooga_Open_Screen.png "The opening window")

The first and second options will open an editing window. In this window, there will be panes on either side of and below a game display window. The left pane will allow the user to modify static aspects of the game, essentially aspects of the game that are present at the start of the game. These include the background, path, size of the map, and static objects, such as rocks, trees or other aspects of scenery that will be displayed when the game is initialized. The right pane will deal with aspects of the game that change after the game begins, such as towers/enemies added by the user and towers/enemies added by the opponent or computer. In this right pane, the user will first need to select the type of game (standard, flipped, multiplayer), and then the user will be able to (in a standard game) select and modify the towers available in-game to the user and the enemies that attack in each wave, as well as the number of waves and wave timing. In the bottom pane, the towers and enemies added to the game will be displayed, and there the user will be able to easily interact with them to change their properties.

![Edit Screen](doc/Vooga_Edit_Screen.png "The editing window") 

The option that allows the user to play an existing game will open a window in which the game will be played. This window will primarily consist of the game map, and there will be a pane on the right side containing the options for the towers or enemies the user can play. Towers/enemies can be dragged from this pane onto the map.

![Play Screen](doc/Vooga_Play_Screen.png "The playing window")

If there is bad data input into the program or a file in the wrong format is selected as an image, a popup will display the error to the user.


## Design Details 

##### Objects (Sprites)

The objects module is primarily intended to handle the genre specific concepts of the project. Though all games feature objects of some sort, our objects package will be set up specifically with tower defense in mind. Objects will keep track of their own properties, such as location, team association, damage and health points, and associated animations and collision events. Since objects will have images associated with them, this module will need access to image resources. As such, the module will have a number of collaborating modules: 
 * objects will have locations stored as positions within the game area and will themselves be displayed within this area
 * objects will have specific behavior, for example the path finding behavior of a moving agent or collision, delegated to the behavior module
 * objects will be displayed within the play display
 * the state of objects will be saved and loaded back by the I/O module
 * objects will be placed into the game area by the edit display module
 * the properties of objects will be editable within the edit display module

Due to these collaborations, the objects’ API will need internal game engine methods as well as external property manipulation methods for the authoring environment. When an object is placed, it will be passed its grid location at construction time. Other properties of the object will be set to some defaults at construction time. Within the authoring environment, the author of the game will be able to change these default properties; this means there will be API methods for updating properties of an object. The construction of game objects will be handled by factories. 

This module is necessary because it will give us flexibility in the types of tower defense elements we include: we will be able to feature many different types of towers and damage-dealing agents with varying properties (for example, some could move while others are stationary). It will also make it easy to both generate objects during the game authoring phase, serialize these objects into a save-file format, and load back in the objects into Java objects when the game is actually run. 

##### Behavior

The behavior module will dictate what happens to objects or what decisions objects will make. Concretely, this consists of an ElementManager class (and potentially other supporing classes as deemed necessary for modularity and extended functionality) which has the following responsibilities:

* Serving as the Single Source of Truth (SSOT) for the set of currently active TowerDefenseElements (which can be queried by the GamePlayer in its game loops to update the display accordingly, among other things)
* Checking for collisions among these active elements
* Handling each collision based on the editor-specified collision logic of each element (through a Visitor Pattern, for example) and mutating the elements
* Moving each element based on its editor-defined movement strategy (path-finding)
* Providing an interface to the front end (GamePlayer) for querying various aspects of game status (lives left, resources left, current level, win / loss status, etc)
* Providing an interface to Objects (Sprites) classes for querying of other elements (for instance, for a tower to query the set of monsters within a certain radius of its position)
* Applying editor-specified victory and defeat conditions to determine game status

Thus, this module will collaborate internally with the Objects (Sprites) module for creation of elements and for updating of elements. It will collaborate (be a source of data for) externally with the Play Display module for providing the current set of active elements, for providing the latest game status, and so on.

Extensions for the behavior module would be partly in terms of fine-grained victory/defeat conditions, initially through an expanding set of finite strategies implemented by various classes implementing an interface, but potentially even through running of author-provided scripts within the isWon() / isLost() methods. Greater flexibility / variety in path-finding and per-projectile / per-element collision customization would be another area of extension. These will be encapsulated within this module primarily by addition of new classes that implement an interface (such as the Behavior.java interface in docs/api) with overridden implementation of the API methods.

This module is necessary because it ensures atomicity of updates, handles element-specific update logic and allows flexibility / extensibility for various game elements and game types. For instance, different kinds of element movement patterns, on-collision projectile behaviors, imperviousness of specific projectiles to specific element types, etc. It also abstracts away all such complex logic from the rest of the program, especially the player, so that once a game is authored, the authored rules are simply enforced through the Behavior module from the perspective of other modules. This way, modularity and abstraction boundaries are also maintained through this module.

##### Game Area

The game area module is designed to demonstrate the layout of a level created within the authoring environment and to associate objects with positions for the game engine’s future level reconstruction. In order to improve the ease of use for a user defining some sort of layout, the game area will consist of several small cells which the user will be able to place an object into. Every object that a user can place or define for later usage must occupy at least one full cell. At least for the basic implementation, this game area will exist at some fixed size with defined boundaries at the edge of the area and will only resize the cells when the window is resized rather than adding new rows of cells and potentially disrupting the relative layout. Eventually we can give the user the flexibility to implement scrolling by interacting with the edge of the game area and adding an interactive row or column of cells depending on where the user clicked.

When a Sprite is placed into one of these cells, the location of the object will be set to be the x and y coordinates of the center of the cell that the user is interacting with and will always snap to this location so there are no objects that can only exist in half of a cell. Cells will need to be designed to display the ImageView of the object that is being placed there once the user initiates the dropping of an object and assigns coordinates; similarly, the overall game view needs to be able to have some sort of background image resource that the user can interact with.

In terms of tracking more complex objects such as a path, the game area will be initially designed to simply incorporate anything unique as some fixed object that exists at the center of the cell. Paths, for instance, will simply consider the center point of a selected cell as an added waypoint to some list of waypoints that constitutes the path. This way the user can eventually turn off the grid view for increased flexibility in object placement and still have waypoint functionality use an identical implementation.

##### Edit Display

The editing display module constitutes the windows that will handle interacting with the game area, placing objects into the region, and setting general high level rules for the game. It represents the true functional region of the authoring environment that the user interacts with and is thus the most crucial component for level construction. There will be three main components that comprise this module. First, the tools sub-window will be where the user actually is able to choose which Sprite or Object they are dragging and dropping into the game area. This component will need to be able to access some default objects that all games will have access, whatever new user-defined components were set during previous modifications in the authoring environment, and will need some prompt that can allow the user to create a new object. New object creation will allow a user to specify an image file or URL to associate with the new object as well as a set of properties that can be modified based off a few general types (tower, goal, obstacle) that they might be selecting from. Also, in addition to being able to drag, drop, and select a component to modify, clicking on one of these object types should allow the user to modify the overall parameters for this Sprite type. All of these components will rely on interacting with the EditController API. Adding in more complex properties and rules to be able to modify will be one way in which we can extend this type of editing.

Below this section will need to be the level editor. This is where rules, opponents, end game conditions, and timing/waves will be specified. This type of modification will need to offer more user flexibility than the previous window, as the user is no longer just specifying certain numerical values or switching a checkbox on and off, though we can only give so much flexibility to the user determining the high level conception of the game. Modifying this process will in the future allow us to incorporate different rule sets for various additional genres, and the design will be left open enough in terms of having each level contain its own rule set and objects that it manages so that there can be “special” levels between rounds that are different from the rest of the game.

Finally, this module will need to track which Sprites and Objects have already been placed and give the user access to these properties so that they can still be modified after the initial creation. 

##### Play Display

Similarly to the game area, the Play Display module is the layout display for the Sprites and Objects that the IO module will have loaded in from some authored game. However, unlike the Game Area, this module has to keep track of two main additional things: user input and animations. Working in conjunction with the Behavior module and API, the play display will update itself and each of its objects during each cycle of the game loop. Particular animation types will be specified within that overriden update method and will be determined by the behavioral properties that were set during the authoring stage. A lot of potential extensions can exist in this stage, as more complex behavior or behavior relating to additional genres can be incorporated here. In addition to this, the play area needs to make sure that it can update its animations or behaviors depending on what type of interaction is being generated by the interaction engine module. Level structure that is loaded in by the IO should also control some element of the looping animation here about when enemies are generated onto the screen.

In addition to the basic animations, this display needs to handle a few additional windows. Most importantly, there will be some heads up display that will also update on each iteration of the game loop (or when a change is detected, such as for tracking of the number of lives). Generating these objects on the screen will be left relatively easy to call so that some extensibility can be added in terms of either adding split screen displays for multiplayer or for adding additional informational windows. The structure of this section will also be made to look as similar as possible to the edit display for the potential future inclusion of nesting the authoring environment within the engine and allowing for mid-game updates.

This module’s necessity is fairly obvious in the sense that it both communicates information to the player about the state of the game but also in allowing the user to functionally decide how to interact with the program.

##### I/O

The I/O module handles writing to / reading from data files, both for authoring and playing. Concretely, it consists of a couple of APIs, one for authoring (AuthoringPersistence) and one for playing (PlayPersistence), which are used by the engine (AuthoringIOController and PlayIOController respectively) to serve saving and loading requests from the authoring environment and player respectively. 

Any logic related to flushing of serialized strings to files and loading of (JSON / XML) strings from reading of files is encapsulated within this module. This insulates the rest of the program from the notion of files.

However, it should be noted, based on the current design, that the logic for serialization of objects / data structures into JSON / XML strings and conversely, for creation of objects from such strings, is NOT handled by this module. Instead, these are handled by the AuthoringIOController / PlayIOController and ElementFactory respectively.

In terms of extensions, the I/O module could support alternate encodings (JSON vs XML)

The I/O module is thus a utility that collaborates with (provides a persistence service to) the engine to serve requests from the authoring environment and player, in a manner reminiscent of an MVC architecture or request-controller-database architecture. The benefit of this approach is that firstly, requests can be validated through the controller before (over)writing precious storage, and secondly, the authoring environment or player does not have to concern itself with the low-level mechanisms for saving and loading. Instead, from the perspective of these front end agents, all that has to be done is to 'tell' the engine (and thus indirectly the I/O module) to save or load the current game / file name.

This module is thus necessary on a functional level for its purpose of providing persistence and on a design level for the modularity and abstraction of hiding file operations from the rest of the back end and data serialization from the front end.

##### Packaging

The packaging module will handle the exporting of an authored game into an executable file; that is, the main feature this will address is the setting up and running of complete levels/stages. This will involve writing the game area and all objects (and their properties) into a JSON format, which will be extracted into Java objects for the engine to work with when the game is run. As such, packing will collaborate heavily with the game area and object modules. There will also need to be significant collaboration with the behavior module when specifying how the objects will make decisions and interact with each other. Finally, the rules that the author specified, such as end-game/end-level goals, game-over (loss) conditions, and options such as single player and multiplayer will need to be packed up as well (these will live in the behavior module). The re-loading of objects written to JSON will require some kind of reflection scheme. Obviously, the packaging module will need to interact heavily with the I/O module, since it needs to package up the JSON files generated by the I/O module. 

Packaging helps us achieve one of our key goals: usefulness of the authoring environment. It could hardly be called flexible if whenever you wanted to play a game generated by the authoring environment, you needed to go through the authoring environment itself; that is, the playing of the game should be entirely independent of its authoring. So packaging up the game will allow an author to actually distribute the game without the players knowing anything about the authoring environment. To achieve this, the module will need to be selective in its packaging: only resources (such as images) that were actually used by the author should be included; the game engine will be required for any game type, so it will always be packaged; not all game object classes will be needed for each game made, so the packaging can reduce unnecessary space usage by packaging only the classes that the game will need. By doing all of this, game authors will be able to send out a game for distribution without players worrying about how the game was implemented. 

##### Networking 

The networking module will handle the infrastructure and logic for supporting multi-player games with updates made by a player immediately visible to all other players. The low-level functionality of such a push-based communication system will be achieved primarily through the use of a 3rd-party Java Websocket protocol library. 

However, the design challenge lies in the encoding and decoding of player actions before sending and after receiving through the sockets, as well as in the direction of data flow. On a high-level, the plan is for the Networking module to consist of 2 main parts:

1. Dataflow: a peer-to-peer broadcast system where a given player's action is broadcasted through the websockets to other players
2. Data representation: a serialization protocol for any update (movement of an element, placement of a tower, etc) which can be used to transmit a string representation of every player's update

When the networking module is complete and ready for integration, its API methods will be called by the Behavior module's interface methods, thus modifying implementation without changing signatures or breaking any client code. For instance, once an update is calculated by the behavior module, it is synchronized to other clients by calling the Networking module's methods.

Since these components are almost entirely independent of the core game logic, it makes sense for the rest of the program to be oblivious to this extension and for it to be encapsulated within the networking module.

##### Interaction Engine 

We plan to feature two methods of interacting with the game while playing. The first option is the keyboard and mouse (or touchscreen for machines with support): the user will be able to place resources primarily using the mouse, and will use the keyboard for certain types of events they wish to initiate. Another type of interaction we plan to include is the standard game controller, which will be plugged into the player’s computer. This will allow for greater placement precision than an ordinary mouse would; for a game where the controls end up being complex (e.g., depend on many keys), the controller will drastically increase playability for players not used to the keyboard setup. 

Supporting the keyboard and mouse interaction will involve the author specifying how initiate certain types of events (for example, activating a power-up).  Though the default method of resource placement in-game will be the same as in the authoring environment, drag-and-drop, we may decide to give flexibility of choosing other means of object placement, namely point-and-click.  Supporting game controllers will involve translation of the key pressed and mouse events used in the game; the author will have the option to supply how the controller should be used, but there will be default translation available available in the case that they do not. For example, by default the drag-and-drop placement of resources will be achieved by holding down a controller button, using the joystick to drag the resource to the desired location, and finally releasing the controller button. For buttons, we plan to include pause-menu support for changing control mappings as part of our second sprint.

Since the interaction engine will be used to manipulate the game’s state, it will need to collaborate heavily with the behavior module. In doing so, it will need to collaborate (at least indirectly if not directly) with the object module. Finally, since things like pausing, saving, and loading will require specific user action that ideally will translate over to any of interaction tools we ultimately include as options, the module will need to collaborate with the play display (for example, to tell the pause screen to display) and the I/O module (for example, to tell it to save the current state of the game).

The interaction engine is included as its own module because it allows for flexibility in how the player interacts with the game. The author will be capable of choosing the default means of interacting with the game, and could enforce this particular interaction method.  However, there will be greater flexibility in allowing the player to actually choose their interaction method, including such details as which keys or controller buttons correspond to particular events. 

## Example games

## Design Considerations 

One design consideration that we are considering is how to let the user define game logic, specifically victory and losing conditions. The first design we are considering is pre-defining logic for these scenarios. For example, we would define logic for the user to lose the game is a certain amount of enemies pass a threshold. The user then selects this losing option and defines the amount of objects that would cause the user to lose. Several other pre-defined scenarios could be if a certain object is destroyed, a certain amount of time passed, or if a certain score is reached. In all of these considerations, the user would merely select certain values or objects for the goal object to compare to. The benefits of this design is it is relatively easy to implement. In order to implement this, a Goal interface could be created that has methods for the Goal to be satisfied. This goal could be positive or negative. Another upside to this design is the goals will, more than likely, be obtainable. If the user can write their own goals in some language, these goals may be unobtainable. The downside to this design is flexibility. With predefined goals, the user would not be able to define any goals for themselves. They would limited to our pre-defined goals

The alternative to the above design would allow the user to define goals using some elementary coding language. By allowing the user to code their own goals, the user will have seemingly unlimited flexibility in designing the goals. This is an ideal scenario, as the goal of creating gamemaker software is to allow the user to create whatever they want. The downside is complexity. By introducing a coding language into the mix, the program will become much more complicated to implement, as we would need to create a compiler and some error checking. Also, the danger with giving the user unlimited control is the user could create an unobtainable or invalid goal.

Another design consideration we discussed was how to export a finished game. This will be the primary way a user will be able to play and share their finished product. One possibility is to create an application that will allow the user to choose from all of their finished games and launch they one they want to play. The benefit of this implementation is not passing the jar files necessary to play the game, specifically the engine files, in the executable game file. This will cause the created game files to be smaller than their stand-alone executable counterparts. The files necessary to run the engine will be present within the startup application, and the game itself can be represented as a group of files that the startup application will interpret to create the objects. The downside of this implementation is anyone who wants to play the game must first have an instance of this application starter. It severely limits the ability for the user to share their creations with others.

The design we are most likely going to go with is having a finished game represented as a stand alone executable file. By having this stand alone executable file, a game can be shared between multiple people without the need of any additional applications. While these files will be much larger than those in the previously discussed implementation, as the game engine will also need to put into the executable, it will allow for every created game to be completely independent and allow for the best user experience.