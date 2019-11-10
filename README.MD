# Exhibitionism
Exhibitionism is tiny class file transformation tool.
Exhibitionism make all class, field and method to public, non final.

## Compile
Clone project to your local machine and open terminal in directory where build.gradle located. Run following command to compile.
```
./gradlew installDist
```

Runnable script and runtime libraries will install under `build/install/Exhibitionism`

## Usage 
Open terminal at `build/install/Exhibitionism/bin` . Execute `Exhibitionism` (Mac/Linux) or `Exhibitionism.bat`(Windows) with following parameters.

### --input
Input file name.

### --output
Output file name. If file already exists, replace original.

### --parallel (Optional, Default=1)
Number of thread used to transform class.

### --nopublic (Flag)
If this flag is set, do not publify.

### --noopen (Flag)
If this flag is set, do not non-final-ify.

### --path (Optional)
Restrict path to be opened. Usually, package name. You can set multiple path with deliminator ','

## Example usage
```
./Exhibitionism --input bukkit.jar --output bukkit-open.jar -parallel 8 --path net.minecraft,org.bukkit.craftbukkit
```