# Definitions

> Note that, generally, XML elements are denoted as `element` while XML attributes should be denoted as `@attribute`.

The CPU definitions file is an XML file that defines address modes and operations. It allows expressions for the byte 
writing and uses regular expressions to match each address mode.

Features to be aware of:

* A CPU can inherit from another CPU. For instance, both the 65C02 and 65816 processor can inherit the 6502 instruction set.
* All documentation components (part of this XML schema) are assumed to be Markdown.
* The output format can have a header that is written first. 
* Many of the components define a value. These are expressions, and they use the same expression language as the assembler.

## General structure

The structure of the XML file defines some general information about the CPU as well as all addressing modes (which also 
define how instructions are written into memory) and operations.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<definition xmlns="https://a2geek/schemas/cpu-definition/2.1"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="https://a2geek/schemas/cpu-definition/2.1 ../schemas/cpu-definition-2.1.xsd">
	
    <!-- Overview detail goes here. -->
    <output-format />
    <address-space />
    <registers />
    <address-modes />
    <operations />
</definition>
```

## Overview detail

### Inheritance

To inherit _all_ registers, address modes, and operations from another processor definition, `definition` has an attribute
`@inherit-from` which specifies the name of the "parent" CPU:

```xml
<definition xmlns="https://a2geek/schemas/cpu-definition/2.1"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="https://a2geek/schemas/cpu-definition/2.1 ../schemas/cpu-definition-2.1.xsd"
            inherit-from="MOS6502">
    <!-- ... -->
</definition>
```

### General detail

Only the `name` entry is required in the overview section.

```xml
<name>WDC65C02</name>
<overview href="http://en.wikipedia.org/wiki/65C02">
The WDC 65C02 8-bit CPU is an upgraded CMOS version of the popular MOS Technology 6502 
microprocessor, the redesign being made by Bill Mensch of the Western Design Center (WDC). 
The 65C02 was second-sourced by NCR, GTE, Rockwell, Synertek and Sanyo.
</overview>
```

## Address space

The address space is generally used to specify how many bits apply to memory, however it can also define memory 
locations for the CPU in question. Note that these become labels available for every program using the CPU definition.

* `@bit-size` is required and indicates the memory size.
* `memory-location` (0 to many) defines any CPU intrinsic values.

```xml
<address-space bit-size="16">
    <memory-location>
        <name>IRQB</name>
        <address>0xfffe</address>
    </memory-location>
    <memory-location>
        <name>RESB</name>
        <address>0xfffc</address>
    </memory-location>
    <memory-location>
        <name>NMIB</name>
        <address>0xfffa</address>
    </memory-location>
</address-space>
```

## Output data

If the output requires a header, one may be specified.

```xml
<output-format>
    <header>
        <!-- Magic bytes 'CPU' (high-bit set) -->
        <byte>'C'|0x80</byte>
        <byte>'P'|0x80</byte>
        <byte>'U'|0x80</byte>
    </header>
</output-format>
```

## Registers

Registers defines and documents the registers available. Depending on how code is generated, this section may be optional. 
(Such as `LDA $1000,X` on the 6502 doesn't actually have to recognize or use a register value since it's part of the
addressing mode for the instruction.) For those CPUs that require a value for the register (such as SWEET16), see the next
example.

Each register has:

* `@id` is required and is the name used in code for the register.
* `@bit-size` is the size of the register.
* `@value` is paired with the group name (`groups`) which becomes a value in the byte generation expression.
* `@groups` is paired with the value, see above.
* `<name>` is a required tag and is a human-readable (possibly more descriptive) name for the accumulator.
* `<description>` is a markdown formatted description.

```xml
<registers>
    <register id="A" bit-size="8">
        <name>Accumulator Register (A)</name>
        <description>
            The Accumulator Register (A) is an 8-bit general purpose register which
            holds one of the operands and the result of arithmetic and logical
            operations. Reconfigured versions of this processor family could have
            additional accumulators.
        </description>
    </register>
    <!-- ... -->
</registers>
```

In the case where register choice impacts the generated instruction code, both `value` and `groups` should be set.

```xml
<registers>
    <register id="R0" bit-size="16" value="0" groups="Rn">
        <name>SWEET 16 accumulator</name>
    </register>
    <register id="R1" bit-size="16" value="1" groups="Rn">
        <name>Register R1</name>
    </register>
    <!-- ... -->
</registers>
```

For example, in the address mode, the `opcode` and the register value can be merged like this: `(opcode&0xf0) | (Rn&0x0f)`.

## Address Modes

The addressing mode defines a processors various instruction formats. It uses a combination of `format` and `pattern` to specify
format and identify where a value (or values) come from. The name in the format is defined as a variable name for the byte's
expression.

Each address mode can have the following:

* `@id` is a unique id used elsewhere in the XML document. Required.
* `title` is a human-readable name.
* `description` is a description (Markdown format).
* `mnemonic-format` is used when an opcode includes information that impacts the generated bytes. See example below.
* `format` is a format _that must match with the `pattern`_ which gives the values a name. See samples.
* `pattern` is used to pull out the expression as well as the variable name from `format`. Note the wildcard is `?`. See samples.
* `constraint` is an expression that helps determine if this address mode is correct. For instance, the 6502 can perform an `LDA`
  against the zero page (0x00..0xFF) or from a memory address (0x0000..0xFFFF). The constraint can be applied to the Zero Page
  reference to help select the correct addressing mode.
* `code` is comprised of `byte` tags and defines the actual code for output.

The `byte` tag has:

* `@mnemonic-match` is used in conjunction with the `mnemonic-format` to generate code. See example below.
* The data component is an expression. Variables that are defined are `opcode` from the operations section and any variables
  specified by the `format` and `regex` definition.

```xml
<address-modes>
    <address-mode id="immediate">
        <title>Immediate Addressing</title>
        <description>
            With Immediate Addressing the operand is the second byte of the
            instruction.
        </description>
        <format>#value</format>
        <pattern>#?</pattern>
        <code>
            <byte>opcode</byte>
            <byte>value</byte>
        </code>
    </address-mode>
</address-modes>
```

Note that if a processor inherits from another processor but needs to define additional address modes, the modes can be shared
by using the `address-mode-reference` tag:

```xml
<address-mode-reference id="immediate" />
```

A sample with the `mnemonic-format` and `constraint` components. This matches the suffix in the opcode mnemonic (such as `LOAD1`,
`LOAD2`, and `LOAD4`). For these samples, the opcode value is ORed with the values 0, 1, or 2. If opcode were 0xF0, this would 
generate a byte of 0xF0, 0xF1, or 0xF2. Note that in the sample, it also generates the low and high value for a 16-bit address.

```xml
<address-mode id="int-addr">
    <title>Integer point instruction with 16-bit address</title>
    <mnemonic-format>.+([124])</mnemonic-format>
    <format>addr</format>
    <pattern>?</pattern>
    <constraint><![CDATA[addr >= 0x0 && addr <= 0xffff]]></constraint>
    <code>
        <byte mnemonic-match="1">opcode | 0x0</byte>
        <byte mnemonic-match="2">opcode | 0x1</byte>
        <byte mnemonic-match="4">opcode | 0x2</byte>
        <byte>addr</byte>
        <byte>addr>>8</byte>
    </code>
</address-mode>
```

## Operations

The operations describe the various instructions for a processor:

* `mnemonic` is the name used in an assembly listing (example: `LDA`, `PUSHA`). Required.
* `description` for documentation, Markdown format.
* `addressing-mode` (0 to many) ties the mnemonic code with the various address modes and assigns the opcode value.

Addressing mode comprises:

* `@ref` references the `id` of the `address-mode`.
* `@opcode` is the byte value to be supplied as the `opcode` value in the address-mode generated code.

For example, the 6502 ADC definitions:

```xml
<operations>
    <operation>
        <mnemonic>ADC</mnemonic>
        <description>Add with carry</description>
        <addressing-mode ref="immediate" opcode="0x69" />
        <addressing-mode ref="zero-page" opcode="0x65" />
        <addressing-mode ref="zero-page-indexed-x" opcode="0x75" />
        <addressing-mode ref="absolute" opcode="0x6d" />
        <addressing-mode ref="absolute-indexed-x" opcode="0x7d" />
        <addressing-mode ref="absolute-indexed-y" opcode="0x79" />
        <addressing-mode ref="zero-page-indirect-x" opcode="0x61" />
        <addressing-mode ref="zero-page-indirect-y" opcode="0x71" />
    </operation>
</operations>
```

