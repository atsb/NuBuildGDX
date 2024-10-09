# NuBuildGDX
NuBuildGDX is a fork of BuildGDX aiming for stability, bug fixing and performance improvements.

My goal is to get it in good shape for long term availability and support.  I will also be personally maintaining some dependencies for this as some parts are very deeply intertwined with API versions and I don't trust upstream projects to not keep changing API's every release they do.

For renderer's, an improved classic renderer is the only option.

# Hardware
1.19.0 supports:

Windows (arm64 and x86_64)

GNU/Linux (arm64, x86_64) - arm64 has a seperate package due to classpath issues I ran into when testing it on an rpi3.

macOS (I do own one and it is my main machine) - the issue here is that libgdx 1.12.x has broken the SharedLibraryLoader.  It is missing in that release but the class definition is still inside the gdx-lwjgl3 backend and that causes a ClassNoDef error when using that version.  1.9.14 has no arm64 macOS native libraries so won't run on an M1.  I'm waiting on libGDX to release a new version and then, I hope to bring back full macOS support.

# Summary
Reduce the bloat, increase the stability..  basically.

# Icon
The icon is taken from: https://www.flaticon.com/free-icon/nuclear_9708569
