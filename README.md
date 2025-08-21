
# Tonnyi Esolang Interpreter

A Java-based interpreter for **Tonnyi**, a custom educational assembly-like esolang (esoteric programming language). Programs are written using **pure binary opcodes** (7-bit) and operands.

## Features

*   **Pure Binary Syntax:** All instructions are written using their 7-bit binary opcode strings (`0b0001010`).
*   **7-bit Instruction Set:** 128 possible opcodes, 44 currently implemented.
*   **BigDecimal Precision:** All arithmetic operations use `BigDecimal` for high precision, supporting very large numbers and decimals.
*   **64KB Memory Space:** Addressable memory from `0x0000` to `0xFFFF`.
*   **Structured Operations:** Includes system, memory, arithmetic, bitwise, comparison, control flow, stack, and I/O operations.
*   **Debug Mode:** Built-in debug commands for memory inspection.
*   **Label Support:** For easy control flow and subroutine calls.

## Getting Started

### Prerequisites

*   Java JDK 8 or higher.

### Installation

1.  Clone the repository or download the `Tonnyi.java` file.
    ```bash
     git clone https://github.com/toaha63/Tonnyi.git
     cd Tonnyi
    ```

2.  Compile the Java source code.
    ```bash
    javac Tonnyi.java
    ```

### Usage

1.  **Create a Program:** Write your Tonnyi assembly code in a file with a `.ton` extension. **You must use the binary opcodes.**
    Example: `hello_world.ton`
    ```assembly
    // Load immediate values for 'H', 'e', 'l', 'l', 'o', ' ', 'W', 'o', 'r', 'l', 'd', '!'
    // Format: [BINARY_OPCODE] [OPERANDS]
    0b0000100 0x1000 #72
    0b0000100 0x1001 #101
    0b0000100 0x1002 #108
    0b0000100 0x1003 #108
    0b0000100 0x1004 #111
    0b0000100 0x1005 #32
    0b0000100 0x1006 #87
    0b0000100 0x1007 #111
    0b0000100 0x1008 #114
    0b0000100 0x1009 #108
    0b0000100 0x100A #100
    0b0000100 0x100B #33
    0b0000100 0x100C #0 // Null terminator

    // Print the string starting at address 0x1000
    0b0101000 0x1000

    // Halt the program
    0b0000000
    ```

2.  **Run the Program:**
    ```bash
    java Tonnyi hello_world.ton
    ```
    *Output:* `Hello World!`

3.  **View Help:** Run the interpreter without arguments to see the full instruction set.
    ```bash
    java Tonnyi
    ```

## Language Specification

### Memory and Addressing

*   **Memory:** A hash map simulating 64KB of address space from `0x0000` to `0xFFFF`.
*   **Immediate Values:** Prefixed with `#` (e.g., `#100`, `#3.14159`, `#-42`).
*   **Memory Addresses:** 4-digit hexadecimal values (e.g., `0x001A`, `0xFFFF`).

### Syntax

*   **Instructions:** Written as `BINARY_OPCODE <operand1> <operand2>`
    *   Example: `0b0001010 0x002A 0x002B` (Add the values at `0x002B` to `0x002A`)
*   **Labels:** Defined on their own line, ending with a colon (`:`).
    *   Example: `my_loop:`
*   **Comments:** Start with `//`.
*   **Whitespace:** Instructions are space-separated.

### Complete Instruction Set & Binary Opcodes

#### System Operations
| Binary Opcode  | Description                                      | Example Usage                  |
|----------------|--------------------------------------------------|--------------------------------|
| `0b0000000`    | Stops program execution.                         | `0b0000000`                    |
| `0b0000001`    | No operation.                                    | `0b0000001`                    |
| `0b0000010`    | Prints all non-zero memory values (Debug only).  | `0b0000010`                    |

#### Memory Operations
| Binary Opcode  | Description                                      | Example Usage                  |
|----------------|--------------------------------------------------|--------------------------------|
| `0b0000011`    | Prints the value at an address.                  | `0b0000011 0x001F`             |
| `0b0000100`    | Loads immediate value into destination.          | `0b0000100 0x0020 #100`        |
| `0b0000101`    | Copies value from source to destination.         | `0b0000101 0x0021 0x0020`      |
| `0b0000110`    | Alias for `LOAD FROM MEMORY`.                    | `0b0000110 0x0022 0x0021`      |
| `0b0000111`    | Alias for `LOAD FROM MEMORY`.                    | `0b0000111 0x0023 0x0022`      |
| `0b0001000`    | Swaps the values at two addresses.               | `0b0001000 0x002A 0x002B`      |
| `0b0001001`    | Sets the value at an address to zero.            | `0b0001001 0x002C`             |

#### Arithmetic Operations
| Binary Opcode  | Description                                      | Example Usage                  |
|----------------|--------------------------------------------------|--------------------------------|
| `0b0001010`    | `dest = dest + src`                              | `0b0001010 0x002A 0x002B`      |
| `0b0001011`    | `dest = dest - src`                              | `0b0001011 0x002A 0x002B`      |
| `0b0001100`    | `dest = dest * src`                              | `0b0001100 0x002A 0x002B`      |
| `0b0001101`    | `dest = dest / src`                              | `0b0001101 0x002A 0x002B`      |
| `0b0001110`    | `dest = dest % src`                              | `0b0001110 0x002A 0x002B`      |
| `0b0001111`    | `addr = addr + 1`                                | `0b0001111 0x002D`             |
| `0b0010000`    | `addr = addr - 1`                                | `0b0010000 0x002D`             |
| `0b0010001`    | `dest = dest ^ src`                              | `0b0010001 0x002A 0x002B`      |
| `0b0010010`    | `addr = -addr`                                   | `0b0010010 0x002E`             |
| `0b0010011`    | `addr = \|addr\|`                                | `0b0010011 0x002F`             |

#### Bitwise Operations
*Uses two's complement representation for negative numbers.*
| Binary Opcode  | Description                                      | Example Usage                  |
|----------------|--------------------------------------------------|--------------------------------|
| `0b0010100`    | `dest = dest & src` (bitwise)                    | `0b0010100 0x0030 0x0031`      |
| `0b0010101`    | `dest = dest \| src` (bitwise)                   | `0b0010101 0x0030 0x0031`      |
| `0b0010110`    | `dest = dest ^ src` (bitwise)                    | `0b0010110 0x0030 0x0031`      |
| `0b0010111`    | `addr = ~addr` (bitwise)                         | `0b0010111 0x0032`             |
| `0b0011000`    | `dest = dest << shift`                           | `0b0011000 0x0033 0x0034`      |
| `0b0011001`    | `dest = dest >> shift` (arithmetic)              | `0b0011001 0x0033 0x0034`      |

#### Comparison & Control Flow
| Binary Opcode  | Description                                      | Example Usage                  |
|----------------|--------------------------------------------------|--------------------------------|
| `0b0011010`    | Compares values at two addresses. Sets internal flag. | `0b0011010 0x0040 0x0041`   |
| `0b0011011`    | Unconditionally jumps to a label.                | `0b0011011 my_label`           |
| `0b0011100`    | Jumps if last comparison was equal (`== 0`).     | `0b0011100 loop_start`         |
| `0b0011101`    | Jumps if last comparison was not equal (`!= 0`). | `0b0011101 error_handler`      |
| `0b0011110`    | Jumps if last comparison was equal (`== 0`).     | `0b0011110 check_passed`       |
| `0b0011111`    | Jumps if last comparison was not equal (`!= 0`). | `0b0011111 check_failed`       |
| `0b0100000`    | Jumps if last comparison was greater (`> 0`).    | `0b0100000 continue_loop`      |
| `0b0100001`    | Jumps if last comparison was less (`< 0`).       | `0b0100001 end_loop`           |
| `0b0100010`    | Pushes current PC to stack and jumps to a label. | `0b0100010 calculate_sqrt`     |
| `0b0100011`    | Pops return address from stack and jumps to it.  | `0b0100011`                    |

#### Stack Operations
| Binary Opcode  | Description                                      | Example Usage                  |
|----------------|--------------------------------------------------|--------------------------------|
| `0b0100100`    | Pushes the value at an address onto the call stack.| `0b0100100 0x0050`            |
| `0b0100101`    | Pops a value from the call stack into an address. | `0b0100101 0x0051`            |

#### I/O Operations
| Binary Opcode  | Description                                      | Example Usage                  |
|----------------|--------------------------------------------------|--------------------------------|
| `0b0100110`    | Reads a number from stdin into an address.       | `0b0100110 0x0060`             |
| `0b0100111`    | Prints the value at an address as an ASCII char. | `0b0100111 0x0061`             |
| `0b0101000`    | Prints a null-terminated string starting at an address. | `0b0101000 0x1000`      |

#### Special Operations
| Binary Opcode  | Description                                      | Example Usage                  |
|----------------|--------------------------------------------------|--------------------------------|
| `0b0101001`    | Stores a random number between 0-100 in an address. | `0b0101001 0x00FF`           |

#### Debug Operations
| Binary Opcode  | Description                                      | Example Usage                  |
|----------------|--------------------------------------------------|--------------------------------|
| `0b0101010`    | Enables verbose debug output.                    | `0b0101010`                    |
| `0b0101011`    | Disables verbose debug output.                   | `0b0101011`                    |

## Example Program: Add Two Numbers

This program reads two numbers from the user, adds them, and prints the result.

```assembly
// Enable debug mode for clearer output
0b0101010

// Prompt for first number: "Enter a: "
0b0000100 0x1100 #69   // 'E'
0b0000100 0x1101 #110  // 'n'
0b0000100 0x1102 #116  // 't'
0b0000100 0x1103 #101  // 'e'
0b0000100 0x1104 #114  // 'r'
0b0000100 0x1105 #32   // ' '
0b0000100 0x1106 #97   // 'a'
0b0000100 0x1107 #58   // ':'
0b0000100 0x1108 #32   // ' '
0b0000100 0x1109 #0    // Null terminator
0b0101000 0x1100       // PRINT STRING

// Read first number into 0x1000
0b0100110 0x1000       // INPUT

// Prompt for second number: "Enter b: "
0b0000100 0x1100 #69   // 'E'
0b0000100 0x1101 #110  // 'n'
0b0000100 0x1102 #116  // 't'
0b0000100 0x1103 #101  // 'e'
0b0000100 0x1104 #114  // 'r'
0b0000100 0x1105 #32   // ' '
0b0000100 0x1106 #98   // 'b'
0b0000100 0x1107 #58   // ':'
0b0000100 0x1108 #32   // ' '
0b0000100 0x1109 #0    // Null terminator
0b0101000 0x1100       // PRINT STRING

// Read second number into 0x1001
0b0100110 0x1001       // INPUT

// Add the numbers: 0x1002 = 0x1000 + 0x1001
0b0000101 0x1002 0x1000 // LOAD FROM MEMORY (Copy value)
0b0001010 0x1002 0x1001 // ADD

// Print the result prompt: "Result: "
0b0000100 0x1100 #82   // 'R'
0b0000100 0x1101 #101  // 'e'
0b0000100 0x1102 #115  // 's'
0b0000100 0x1103 #117  // 'u'
0b0000100 0x1104 #108  // 'l'
0b0000100 0x1105 #116  // 't'
0b0000100 0x1106 #58   // ':'
0b0000100 0x1107 #32   // ' '
0b0000100 0x1108 #0    // Null terminator
0b0101000 0x1100       // PRINT STRING

// Print the numeric result
0b0000011 0x1002       // PRINT

// Halt the program
0b0000000
```

## Contributing

Contributions are welcome! Feel free to fork the repository and submit pull requests for:
*   New instructions (using the remaining 7-bit opcodes).
*   Bug fixes.
*   Performance improvements.
*   Additional features.

## License

This project is licensed under the MIT License.

---