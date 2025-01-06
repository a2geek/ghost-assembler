# Sweet 16 CPU


SWEET16 is an interpreted &quot;byte-code&quot; language invented by Steve Wozniak and implemented
as part of the Integer BASIC ROM in the Apple II computer. It was created because Wozniak
needed to manipulate 16-bit pointer data in his implementation of BASIC, and the Apple II
was an 8-bit computer.

SWEET16 code is executed as if it were running on a (non-existent) 16-bit processor with
sixteen, internal 16-bit little-endian registers, R0 through R15. Some registers have
well-defined functions:

* R0 is the accumulator.
* R14 is the status register.
* R13 stores the result of all comparison operations for branch testing.
* R15 is the program counter.

The 16 virtual registers, 32 bytes in total, are located in the zero page of the
Apple II&#39;s real, physical memory map (at $00-$1F). The actual SWEET16 interpreter is
located from $F689 to $F7FC.

According to Wozniak, the SWEET16 implementation is a model of frugal coding, taking
up only about 300 bytes in memory. SWEET16 runs about 10 times slower than the equivalent
native 6502 code.



## Registers

| Name | ID | Bits | Description |
| --- | --- | --- | :--- |
| SWEET 16 accumulator | R0 | 16 |  |
| Register R1 | R1 | 16 |  |
| Register R3 | R2 | 16 |  |
| Register R3 | R3 | 16 |  |
| Register R4 | R4 | 16 |  |
| Register R5 | R5 | 16 |  |
| Register R6 | R6 | 16 |  |
| Register R7 | R7 | 16 |  |
| Register R8 | R8 | 16 |  |
| Register R9 | R9 | 16 |  |
| Register R10 | R10 | 16 |  |
| Register R11 | R11 | 16 |  |
| Subroutine return stack pointer | R12 | 16 |  |
| Compare instruction results | R13 | 16 |  |
| Status register | R14 | 16 |  |
| Program counter (PC) | R15 | 16 |  |

## Addressing Modes

| Title | ID | Format | Bytes | Description |
| --- | --- | --- | --- | --- |
|  | register-immediate | Rn,value | 3 |  |
|  | register-indirect | @Rn | 1 |  |
|  | register-direct | Rn | 1 |  |
|  | relative-branch | addr | 2 |  |
|  | none |  | 1 |  |

## Operations

| Mnemonic | Description | Opcode | Format | ID | Bytes |
| --- | --- | --- | --- | --- | --- |
| SET | Set constant | 0x10 | Rn,value | register-immediate | 3 |
| LD | Load | 0x20 | Rn | register-direct | 1 |
| LD | Load | 0x40 | @Rn | register-indirect | 1 |
| ST | Store | 0x30 | Rn | register-direct | 1 |
| ST | Store | 0x50 | @Rn | register-indirect | 1 |
| LDD | Load double indirect | 0x60 | @Rn | register-indirect | 1 |
| STD | Store double indirect | 0x70 | @Rn | register-indirect | 1 |
| POP | Pop indirect | 0x80 | @Rn | register-indirect | 1 |
| STP | Store POP indirect | 0x90 | @Rn | register-indirect | 1 |
| ADD | Add | 0xa0 | Rn | register-direct | 1 |
| SUB | Subtract | 0xb0 | Rn | register-direct | 1 |
| POPD | Pop double indirect | 0xc0 | @Rn | register-indirect | 1 |
| CPR | Compare | 0xd0 | Rn | register-direct | 1 |
| INR | Increment | 0xe0 | Rn | register-direct | 1 |
| DCR | Decrement | 0xf0 | Rn | register-direct | 1 |
| RTN | Return to 6502 mode | 0x00 |  | none | 1 |
| BR | Branch always | 0x01 | addr | relative-branch | 2 |
| BNC | Branch if no carry | 0x02 | addr | relative-branch | 2 |
| BC | Branch if carry | 0x03 | addr | relative-branch | 2 |
| BP | Branch if plus | 0x04 | addr | relative-branch | 2 |
| BM | Branch if minus | 0x05 | addr | relative-branch | 2 |
| BZ | Branch if zero | 0x06 | addr | relative-branch | 2 |
| BNZ | Branch if nonzero | 0x07 | addr | relative-branch | 2 |
| BM1 | Branch if minus 1 | 0x08 | addr | relative-branch | 2 |
| BNM1 | Branch if not minus 1 | 0x09 | addr | relative-branch | 2 |
| BK | Break | 0x0a |  | none | 1 |
| RS | Return from subroutine | 0x0b |  | none | 1 |
| BS | Branch to subroutine | 0x0c | addr | relative-branch | 2 |
