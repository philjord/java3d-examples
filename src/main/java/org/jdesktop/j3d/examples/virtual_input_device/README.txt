        Java 3D (TM) Input Device Driver Development Guide
               
Topics

  * Write Once, Run Anywhere (TM)
  * Overview of the InputDevice and Sensor APIs
  * Recipe for an Application Program that Uses Input Devices
  * Location for Installation of Device Drivers
  * Using a Preexistent Native Driver
  * Package Naming Conventions
  * Device Driver Constructor
  * Driver Scheduling and Blocking Semantics 


Write Once, Run Anywhere

Platform independence is the cornerstone of the Java (TM) platform.
This vision now extends to external input devices as well.  The
overarching goal of the Java 3D input device architecture is to enable
Java programs that use devices to run in a platform independent
manner.  

We encourage developers to use Java APIs for their drivers.  APIs such
as the javax.comm API allow platform independent access to serial and
parallel ports.  However, even if a driver is partially written with
native code, the Java 3D InputDevice interface is layered on top of the
driver such that once the native portion of the driver has been
installed into the local JRE, the application code is platform
independent.  

In a future release, the Java 3D team is going to release a registry
mechanism that will reside in the JRE as part of the Java 3D
installation and allow registration of device drivers.  The
SimpleUniverse utility will be modified to allow querying of devices by
generic characteristics, and will subsequently look up and instantiate
the appropriate device driver registered with the registry mechanism.
The Java 3D team also expects to release a set of generic mappings for
devices.  This will enable applications to count on the same behavior
from different drivers for similar types of devices.  There will also
be personalized profiles that enable a user to specify device mappings
for features like dominant hand preference and eye position.  


Overview of the InputDevice and Sensor APIs

Java 3D abstracts the concept of a device via the InputDevice
interface.  The developer's implementation of the InputDevice interface
is layered on top of the device driver.  The device may be a real
device such as a joystick or it may be a virtual device such as a piece
of Java code that does transform calculations.

A device sends data back to Java 3D by placing values into Sensor
objects which the device code manages and updates.  The Sensor class
encapsulates a transform and a timestamp.  The transform is specified
in the TrackerBase coordinate system.

Java 3D schedules calls to the device's pollAndProcessInput routine,
which is a method in the InputDevice interface.  This method is
responsible for updating the devices sensor values.  The sensors'
values and time stamps are read by a user's behavior code and/or each
frame (by the Java 3D implementation) when head tracking is enabled.
There are several options for scheduling, and they are detailed in the
InputDevice javadoc and in the section below entitled "Driver
Scheduling and Blocking Semantics."

Please read the javadocs for InputDevice and Sensor for more detailed
information.  There is also a sample program in the Java 3D release
called VirtualInputDevice, which implements these concepts in Java code.


Recipe for an Application Program that Uses Input Devices

Please see the Java 3D example program in the examples directory called
VirtualInputDevice for an example of using this code recipe:
  1) Implement the InputDevice interface with a class of your own
  2) Call the device's constructor from your main program
  3) Call initialize() on the device
  4) Call PhysicalEnvironment.addInputDevice(InputDevice) or if you are
     using SimpleUniverse, call SimpleUniverse.getViewer().
     getPhysicalEnvironment().addInputDevice(InputDevice)
  5) Assuming you want to modify the viewer's transform with the device:
     add a WakeupOnElapsedFrames behavior to your scene graph that wakes
     up every frame and in the processStimulus method modify the view 
     transform with the transform you pull out of Sensor.getRead.
    
In a future release, it will be possible to replace steps 2, 3, & 4 with
a single method call to the SimpleUniverse utility. 
     

Location for Installation of Device Drivers

There are two suggested ways to package and distribute drivers.

If a driver is written entirely in Java and if it is tightly coupled
with a particular application without the expectation of reuse in other
applications, then it should be bundled and distributed with the
application itself.

If a driver is not associated with any particular application program,
if it contains any native code, or if it is expected to be used by more
than one application program, then it should be installed directly into
the end user's local JRE.  It is expected that most drivers for real
devices fall into this category.  On the Solaris platform, the Java
portion of the driver should be installed into jre/lib/ext as a
uniquely named jar file and if there is native code it should be
compiled into a shared object and installed into jre/lib/sparc.  On the
Win32 platform, the Java portion of the driver should be installed into
jre\lib\ext as a uniquely named jar file and if there is native code it
should be compiled into a standard dynamically linked library (dll) and
installed into jre\bin.  


Using a Preexistent Native Driver

It is possible to make a Java 3D driver out of a preexistent native
driver.  In order to do this, you need to create an InputDevice
interface that uses JNI to access the associated native driver methods
whenever the corresponding InputDevice interface method is called from
Java 3D.  The native portion of the driver must be installed into the
target JRE.


Package Naming Conventions

All device drivers that are installed into the JRE should be part of a
package that follows both standard Java and Java 3D naming
conventions.  For instance, an input device driver should be placed
into a package called
com.<company_name>.j3d.drivers.input.<device_name>.  The package should
be jarred up into a jar file that has a unique name.

Any native .so or .dll files installed into the JRE should be uniquely
named.


Device Driver Constructor

The constructor arguments for a device driver must be an array of
strings.  So a driver should have a single public constructor that
takes an array of strings.  The idea behind this requirement is that
eventually the Java 3D registry will contain an array of string
arguments to be sent to the device constructor at instantiation time.
The SimpleUniverse API will also make a provision for optional String
arguments to be added to the array of String arguments found in the
registry.


Driver Scheduling and Blocking Semantics

When a device is registered with Java 3D via the
PhysicalEnvironment.addInputDevice(InputDevice) method call,
InputDevice.getProcessingMode() is called on the registered device.
This method should return one of the three processing modes defined in
the InputDevice interface:  BLOCKING, NON_BLOCKING, and DEMAND_DRIVEN.

  BLOCKING signifies that the driver for a device is a blocking driver
  and that it should be scheduled for regular reads by Java 3D. A
  blocking driver is defined as a driver that can cause the thread
  accessing the driver (the Java 3D implementation thread calling the
  pollAndProcessInput method) to block while the data is being accessed
  from the driver.

  NON_BLOCKING signifies that the driver for a device is a non-blocking
  driver and that it should be scheduled for regular reads by Java 3D.
  A non-blocking driver is defined as a driver that does not cause the
  calling thread to block while data is being retrieved from the
  driver.  If no data is available from the device, pollAndProcessInput
  should return without updating the sensor read value.

  DEMAND_DRIVEN signifies that the Java 3D implementation should not
  schedule regular reads on the sensors of this device; the Java 3D
  implementation will only call pollAndProcessInput when one of the
  device's sensors' getRead methods is called. A DEMAND_DRIVEN driver
  must always provide the current value of the sensor on demand
  whenever pollAndProcessInput is called. This means that DEMAND_DRIVEN
  drivers are non-blocking by definition.

It is important that you correctly classify your driver.  If it is a
NON_BLOCKING driver, most Java 3D implementations will choose to add
inertia inside the scheduling thread to avoid starvation of the other
Java 3D threads.  If it is a BLOCKING driver, most Java 3D
implementations will choose to spawn a separate scheduling thread for
each BLOCKING device.  If your driver is a DEMAND_DRIVEN driver, your
driver must always provide the current value upon request along with
the current time stamp.

When running drivers with the Solaris operating system using the
Solaris reference 1.2 JRE and green threads, you should be aware that
there is a bug that forces all drivers to be BLOCKING.  Thus, you
should be careful to always use native threads on the Solaris reference
1.2 JRE in order to get the expected behavior.  This is not an issue
with the Solaris 1.2 Performance JRE release, which is native threads
only.


