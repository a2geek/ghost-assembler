# Directives

Directives are pseudo opcodes used to control the state of the assembler and the code generated.  They may change
the target location for the assembler or switch to a new CPU definition.  They are executed at assembly time, not
at run time.  In the Assembler project, they are dynamically discovered and extensible, see the 
"Adding directives" section below for details.

## .byte - Bytes
Place the given byte values into memory.

Example:

```
.byte 0x00,0x01,0x02,0x03
```

Places `0x00 0x01 0x02 0x03` into memory.
## .org - Change the code address origin
The `.org` directive indicates which memory locations the assembler should begin with when assembling code. 
There can be multiple `.org` directives in the source file.

Example:

```
.org 0x300
```

Note that this may interact with the `.cpu` directive if that particular CPU has some form of signature bytes
that are required at the beginning of the file. 
## .asciih - ASCII Text (high-bit set)
Place the given ASCII string into memory with the high-bit set. Note that numerical constants may follow pseudo-opcode.

Example:

```
.asciih "Hello",0x00
```

Places `0xC8 0xE5 0xEC 0xEC 0xEF 0x00` into memory.
## .elif - Conditional ELSE-IF
Test another expression and include following code if expression is non-zero. 
Must follow `.if` directive and occur before `.else` or `.endif`.

See `.if` for example.
## .stringh - String Text (high-bit set)
Place the given ASCII string into memory, beginning with a length byte with the high-bit set.
Note that numerical constants may follow pseudo-opcode.

Example:

```
.stringh "Hello"
```

Places `0x05 0xC8 0xE5 0xEC 0xEC 0xEF` into memory.
## .ascii - ASCII Text
Place the given ASCII string into memory without the high-bit set. Note that numerical constants may follow pseudo-opcode.

Example:

```
.ascii "Hello",0x00
```

Places `0x48 0x65 0x6C 0x6C 0x6F 0x00` into memory.
## .define - Define a variable
Place a name into the global name space with no value. This can be tested with the `.if` directive.

Example:

```
.define debug

.if debug
; do something
.endif
```
## .endif - End conditional IF
Close out `.if` directive.

See `.if` for example.
## .if - Conditional IF
Test the given expression and include following code if expression is non-zero. Must be first directive.

Example:

```
.if var=1
lda #0xff
.elif var=2
lda #0x80
.else
lda #0
.endif
```

Note that nested `.if` directives are not allowed at this time.
## .string - String Text
Place the given ASCII string into memory, beginning with a length byte. Note that numerical constants may follow pseudo-opcode.

Example:

```
.string "Hello"
```

Places `0x05 0x48 0x65 0x6C 0x6C 0x6F` into memory.
## .cpu - Load CPU definition
Multiple `.cpu` directives may be used in the source file to switch between CPU definitions
(for instance, on the Apple II series to switch between 6502 and Sweet-16).

### .cpu <name>

Load CPU definition from the predefined list.

Example (from [Porting Sweet 16](http://www.6502.org/source/interpreters/sweet16.htm)):

```
     .cpu <MOS6502>
MOVE JSR SWEET.16
     .cpu <SWEET16>
     SET 1,SOURCE    ; ADDRESS OF SOURCE BLOCK
     SET 2,DESTIN    ; ADDRESS OF DESTINATION BLOCK
     SET 3,N         ; # BYTES TO MOVE
LOOP LD  @1          ; GET BYTE FROM SOURCE
     ST  @2          ; STORE IN DESTINATION
     DCR 3
     BNZ LOOP        ; NOT FINISHED YET
     RTN
     .cpu <MOS6502>
     RTS
```

### .cpu "name"

Load custom CPU from the file system. Relative or absolute path names are allowed.

Example:

```
     .cpu "my_68000.xml"
     moveq #0, d0
     moveq #0, d1
loop move.w (a0)+, d0
     add.l d0, d1
     dbra d2, loop
```
## .else - Conditional ELSE
If test for `.if` or `.elif` was zero, include the following code.

See `.if` for example.
## = - Assignment
Assign a value to a label.

Example:

```
PTR = 0x05
```

## Adding directives

All directives in the Assembler project are dynamically added via the Java `ServiceLoader` mechanism.
This means that, if needed, custom directives can be added into the Assembler.  The usefulness of this will be 
determined by what methods are exposed via the `Directive` interface. 

Generally speaking, to add a new directive, create a class that implements the `Directive`
interface.  In the Java `META-INF` folder, create a `services` folder.  In that, create
a directive file named `a2geek.asm.service.Directive` (full name of interface being implemented).  In that file
list the full name of the class(es).  Create a JAR file, add it to the class path, and it will be
discovered by the Java `ServiceLoader`.

Note that each `Directive` has a method to build a `DirectiveDocumentation` object.
It is in this manner that the directive documentation on this page is dynamically generated.

More details about the `ServiceLoader` interface can be found in the
[Java documentation](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/ServiceLoader.html).
