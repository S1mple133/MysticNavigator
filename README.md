# MysticNavigator

Minecraft Hub Plugin

## Getting Started

This Plugin allows you to set some GameModes up. Every GameMode has its Arenas and players can choose which arena they want to be teleported to.

### Dependencies

MysticNavigator does not have any dependencies.

## Permissions and Commands

```
<Argument> is a must. [Argument] is optional.
/mn help : Display help
/mn add <GameMode> : Create a new GameMode
/mn remove <GameMode> : Remove a GameMode (mn.remove)
/mn setspawn : Set the default spawn (Hub) (mn.setspawn)
/mn addarena <GameMode> <Arena> : Set your location as an Arena of <GameMode> with the Name <Arena> (mn.addarena)
/mn removearena <GameMode> <Arena> : Remove <Arena> from <GameMode> (mn.removearena)
/mn join <GameMode> [Arena] : Teleport yourself to the First Arena (without 2nd Argument) or to <Arena> of <GameMode> (mn.join)
/mn leave : Teleport yourself to the Hub (mn.leave)
/mn backup : Back the database up. (mn.backup)
/mn Arenas <GameMode> : List Arenas of <GameMode> (mn.arenas)
```

```
<Argument> is a must. [Argument] is optional.
/mna help : Display help
/mna create <Arena> : Create a new Arena (Select it with WorldEdit) (mna.arenas.create)
/mna remove <GameMode> : Remove an arena (mna.arenas.remove)
/mna reset : Reset an Arena (mna.arenas.reset)
/mna arenas : List all available Arenas (mna.arenas.arenas)
```
	
## How-To

GameModes Setup:
```
1. Set the default spawn (Hub).
2 Create a GameMode. (You can create more if you want, of course)
3 Give your players access to mn.join, mn.leave and mn.arenas
4 Add some arenas. (You have to add at least one Arena which will be set as the default Arena for the GameMode)
5 Lean back and relax. You are done.
```

Arenas Setup:
```
1. Select a Region with WorldEdit
2. Use /mna create <name>
3. To Reset the arena do /mna reset <name>
```

### Installing

Latest stable build: https://github.com/S1mple133/MysticNavigator/releases
```
Download the .jar file and put it in your server's "plugins" folder.
```

### Arenas
An arena is a way of resetting places (Like a prison server, where the mines get reset)

### Authors

I am the only Author at the moment.

### About Me

Discord: S1mple#8051
```
If you need any self developed plugins I might be able to code them. PM me.

Lately I joined a project named Wackster.    

There you can find help if you are searching for self made plugins, hosting, help with your 
plugin configurations and so on.
```

If you're interested visit our website at http://turpster.xyz/wackster .


## Licence

You are not allowed to share edited Versions of this code and / or compiled .jar Files.

