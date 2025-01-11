# Ghost Assembler

This is a repurposing of an old Java assembler that allows dynamic instruction sets. The initial purpose is to utilize it within the 
[Ghost in the Stack VM](https://github.com/a2geek/ghost-in-the-stack-vm), and it's BASIC compiler - hence the name.

# Overview

The Assembler is a multi-target assembler with CPUs being defined via XML.  Due to this fact, CPUs can be defined
and used without needing to package the CPU definition with the Assembler itself.

Current CPU definitions:

* [6502](doc/MOS6502.md)
* [Sweet 16](doc/Sweet%2016%20CPU.md)
* [65C02](doc/WDC65C02.md)

Current Capabilities:

* Registers with bit-width
* User-defined addressing modes
* Regular Expressions for identifying variable portions
* Expressions to handle basic mathematics and addressing  
* User-defined operations, opcodes, and mnemonics  
* Dynamic set of [directives](doc/directives.md)
