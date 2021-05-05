# Exhibitionism

Exhibitionism is tiny class file transformation tool. Exhibitionism make all class, field and method to public, non
final.

## Compile

Clone project to your local machine and open terminal in directory where build.gradle located. Run following command to
compile.

```
./gradlew installDist
```

Runnable script and runtime libraries will install under `build/install/Exhibitionism`

## Usage

Open terminal at `build/install/Exhibitionism/bin` . Execute `Exhibitionism` (Mac/Linux) or `Exhibitionism.bat`(Windows)
with following parameters.

### --input

Input file name.

### --output

Output file name. If file already exists, replace original.

### --parallel (Optional, Default=Maximum system threads)

Number of thread used to transform class.

### --nopublic (Flag)

If this flag is set, do not publify.

### --noopen (Flag)

If this flag is set, do not open final class/field/method.

### --path (Optional)

Restrict path to be opened. Usually, package name. You can set multiple path with deliminator ','

### --noStaticFinal (Flag)

If this flag set, do not open static final field.

### --ignoreDuplicates (Flag)

If set, Exhibitionism will ignore duplicated files and does not add them to the output file.

## Example usage

```
./Exhibitionism --input bukkit.jar --output bukkit-open.jar -parallel 8 --path net.minecraft,org.bukkit.craftbukkit
```

## API Support

Exhibitionism is hosted in heartpattern maven repository. To use Exhibitionism in your code, first add maven repository.

```groovy
repositories {
    maven {
        url 'https://maven.heartpattern.kr/repository/maven-public/'
    }
}
```

And add dependency

```groovy
dependencies {
    compile 'kr.heartpattern:Exhibitionism:1.0.0-SNAPSHOT'
}
```

API is quite tiny and simple. You can see example usage in App.kt

```kotlin
transform(
    ExhibitionismOptions(
        source = "bukkit.jar",
        destination = "bukkit-open.jar",
        parallel = 8,
        path = setOf("net.minecraft", "org.bukkit.craftbukkit"),
        logger = Logger.getGlobal(),
        public = true,
        open = true,
        noStaticFinal = true
    )
)
```