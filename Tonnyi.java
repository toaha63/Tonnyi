import java.io.*;
import java.math.*;
import java.util.*;

public class Tonnyi
{
    private Map<String, BigDecimal> memory;
    private Stack<BigDecimal> callStack;
    private Map<String, Integer> labels;
    private int programCounter;
    private boolean running;
    private int comparisonResult;
    private boolean debugMode;

    public Tonnyi()
    {
        memory = new HashMap<>();
        callStack = new Stack<>();
        labels = new HashMap<>();
        programCounter = 0;
        running = true;
        comparisonResult = 0;
        debugMode = false;
    }

    public void executeFromFile(String filename)
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename)))
        {
            // First pass: collect labels
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null)
            {
                line = line.trim();
                if (line.endsWith(":"))
                {
                    String label = line.substring(0, line.length() - 1).trim();
                    labels.put(label, lineNumber);
                }
                lineNumber++;
            }

            // Second pass: execute
            programCounter = 0;
            running = true;

            try (BufferedReader execReader = new BufferedReader(new FileReader(filename)))
            {
                String[] lines = execReader.lines().toArray(String[]::new);

                while (running && programCounter < lines.length)
                {
                    line = lines[programCounter].trim();
                    programCounter++;

                    // Skip empty lines, comments, and labels
                    if (line.isEmpty() || line.startsWith("//") || line.endsWith(":"))
                    {
                        continue;
                    }

                    // Remove inline comments
                    if (line.contains("//"))
                    {
                        line = line.substring(0, line.indexOf("//")).trim();
                    }

                    executeInstruction(line);
                }
            }
        }
        catch (IOException e)
        {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    private void executeInstruction(String instruction)
    {
            if (instruction.isEmpty()) return;
            
            String[] parts = instruction.split("\\s+", 3);
            if (parts.length < 1) return;
        
            String rawOpcode = parts[0];
            String[] operands = parts.length > 1 ? 
                    parseOperands(parts.length > 2 ? parts[1] + " " + parts[2] : parts[1]) : 
                    new String[0];

        try
        {
            // One-liner normalization for binary opcodes
            String opcode = rawOpcode.startsWith("0b") ? 
                "0b" + String.format("%7s", Integer.toBinaryString(
                    Integer.parseInt(rawOpcode.substring(2).replaceFirst("^0+", "0"), 2)
                )).replace(' ', '0') : 
                rawOpcode;

            switch (opcode)
            {
                // SYSTEM OPERATIONS (0-2)
                case "0b0000000": // HALT (0)
                    halt();
                    break;
    
                case "0b0000001": // NOP (1)
                    break;
    
                case "0b0000010": // DUMP MEMORY (2)
                    dumpMemory();
                    break;
    
                // MEMORY OPERATIONS (3-9)
                case "0b0000011": // PRINT (3)
                    if (operands.length >= 1)
                    {
                        printMemory(operands[0]);
                    }
                    break;
    
                case "0b0000100": // LOAD IMMEDIATE (4)
                    if (operands.length >= 2)
                    {
                        loadImmediate(operands[0], operands[1]);
                    }
                    break;
    
                case "0b0000101": // LOAD FROM MEMORY (5)
                    if (operands.length >= 2)
                    {
                        loadFromMemory(operands[0], operands[1]);
                    }
                    break;
    
                case "0b0000110": // MOV (6)
                    if (operands.length >= 2)
                    {
                        moveData(operands[0], operands[1]);
                    }
                    break;
    
                case "0b0000111": // STORE (7)
                    if (operands.length >= 2)
                    {
                        storeMemory(operands[0], operands[1]);
                    }
                    break;
    
                case "0b0001000": // SWAP (8)
                    if (operands.length >= 2)
                    {
                        swap(operands[0], operands[1]);
                    }
                    break;
    
                case "0b0001001": // CLEAR (9)
                    if (operands.length >= 1)
                    {
                        clear(operands[0]);
                    }
                    break;
    
                // ARITHMETIC OPERATIONS (10-19)
                case "0b0001010": // ADD (10)
                    if (operands.length >= 2)
                    {
                        arithmeticOperation(operands[0], operands[1], "ADD");
                    }
                    break;
    
                case "0b0001011": // SUBTRACT (11)
                    if (operands.length >= 2)
                    {
                        arithmeticOperation(operands[0], operands[1], "SUB");
                    }
                    break;
    
                case "0b0001100": // MULTIPLY (12)
                    if (operands.length >= 2)
                    {
                        arithmeticOperation(operands[0], operands[1], "MUL");
                    }
                    break;
    
                case "0b0001101": // DIVIDE (13)
                    if (operands.length >= 2)
                    {
                        arithmeticOperation(operands[0], operands[1], "DIV");
                    }
                    break;
    
                case "0b0001110": // MODULO (14)
                    if (operands.length >= 2)
                    {
                        arithmeticOperation(operands[0], operands[1], "MOD");
                    }
                    break;
    
                case "0b0001111": // INCREMENT (15)
                    if (operands.length >= 1)
                    {
                        increment(operands[0]);
                    }
                    break;
    
                case "0b0010000": // DECREMENT (16)
                    if (operands.length >= 1)
                    {
                        decrement(operands[0]);
                    }
                    break;
    
                case "0b0010001": // POWER (17)
                    if (operands.length >= 2)
                    {
                        powerOperation(operands[0], operands[1]);
                    }
                    break;
    
                case "0b0010010": // NEGATE (18)
                    if (operands.length >= 1)
                    {
                        negateOperation(operands[0]);
                    }
                    break;
    
                case "0b0010011": // ABSOLUTE (19)
                    if (operands.length >= 1)
                    {
                        absoluteOperation(operands[0]);
                    }
                    break;
    
                // BITWISE OPERATIONS (20-25)
                case "0b0010100": // AND (20)
                    if (operands.length >= 2)
                    {
                        bitwiseOperation(operands[0], operands[1], "AND");
                    }
                    break;
    
                case "0b0010101": // OR (21)
                    if (operands.length >= 2)
                    {
                        bitwiseOperation(operands[0], operands[1], "OR");
                    }
                    break;
    
                case "0b0010110": // XOR (22)
                    if (operands.length >= 2)
                    {
                        bitwiseOperation(operands[0], operands[1], "XOR");
                    }
                    break;
    
                case "0b0010111": // NOT (23)
                    if (operands.length >= 1)
                    {
                        bitwiseNot(operands[0]);
                    }
                    break;
    
                case "0b0011000": // SHIFT LEFT (24)
                    if (operands.length >= 2)
                    {
                        shiftOperation(operands[0], operands[1], "LEFT");
                    }
                    break;
    
                case "0b0011001": // SHIFT RIGHT (25)
                    if (operands.length >= 2)
                    {
                        shiftOperation(operands[0], operands[1], "RIGHT");
                    }
                    break;
    
                // COMPARISON OPERATIONS (26)
                case "0b0011010": // COMPARE (26)
                    if (operands.length >= 2)
                    {
                        compare(operands[0], operands[1]);
                    }
                    break;
    
                // CONTROL FLOW OPERATIONS (27-35)
                case "0b0011011": // JUMP (27)
                    if (operands.length >= 1)
                    {
                        jump(operands[0]);
                    }
                    break;
    
                case "0b0011100": // JUMP IF ZERO (28)
                    if (operands.length >= 1)
                    {
                        jumpConditional(operands[0], "ZERO");
                    }
                    break;
    
                case "0b0011101": // JUMP IF NOT ZERO (29)
                    if (operands.length >= 1)
                    {
                        jumpConditional(operands[0], "NOT_ZERO");
                    }
                    break;
    
                case "0b0011110": // JUMP IF EQUAL (30)
                    if (operands.length >= 1)
                    {
                        jumpConditional(operands[0], "EQUAL");
                    }
                    break;
    
                case "0b0011111": // JUMP IF NOT EQUAL (31)
                    if (operands.length >= 1)
                    {
                        jumpConditional(operands[0], "NOT_EQUAL");
                    }
                    break;
    
                case "0b0100000": // JUMP IF GREATER (32)
                    if (operands.length >= 1)
                    {
                        jumpConditional(operands[0], "GREATER");
                    }
                    break;
    
                case "0b0100001": // JUMP IF LESS (33)
                    if (operands.length >= 1)
                    {
                        jumpConditional(operands[0], "LESS");
                    }
                    break;
    
                case "0b0100010": // CALL (34)
                    if (operands.length >= 1)
                    {
                        call(operands[0]);
                    }
                    break;
    
                case "0b0100011": // RETURN (35)
                    returnFromCall();
                    break;
    
                // STACK OPERATIONS (36-37)
                case "0b0100100": // PUSH (36)
                    if (operands.length >= 1)
                    {
                        push(operands[0]);
                    }
                    break;
    
                case "0b0100101": // POP (37)
                    if (operands.length >= 1)
                    {
                        pop(operands[0]);
                    }
                    break;
    
                // I/O OPERATIONS (38-40)
                case "0b0100110": // INPUT (38)
                    if (operands.length >= 1)
                    {
                        input(operands[0]);
                    }
                    break;
    
                case "0b0100111": // PRINT CHAR (39)
                    if (operands.length >= 1)
                    {
                        printChar(operands[0]);
                    }
                    break;
    
                case "0b0101000": // PRINT STRING (40)
                    if (operands.length >= 1)
                    {
                        printString(operands[0]);
                    }
                    break;
    
                // SPECIAL OPERATIONS (41)
                case "0b0101001": // RANDOM (41)
                    if (operands.length >= 1)
                    {
                        random(operands[0]);
                    }
                    break;
    
                // DEBUG OPERATIONS (42-43)
                case "0b0101010": // DEBUG MODE ON (42)
                    debugMode = true;
                    break;
    
                case "0b0101011": // DEBUG MODE OFF (43)
                    debugMode = false;
                    break;
    
                default:
                    System.out.println("Unknown opcode: " + opcode);
            }
        }
        catch (Exception e)
        {
            System.out.println("Error executing instruction: " + instruction);
            e.printStackTrace();
            running = false;
        }
    }

    private String[] parseOperands(String operandString)
    {
        return operandString.split("[,\\s]+");
    }

    private boolean isValidAddress(String address)
    {
        if (!address.matches("0x[0-9A-Fa-f]{4}"))
        {
            System.out.println("Error: Invalid address format '" + address + "'. Must be 4-digit hex (0x0000-0xFFFF)");
            return false;
        }

        try
        {
            int addrValue = Integer.parseInt(address.substring(2), 16);
            if (addrValue < 0x0000 || addrValue > 0xFFFF)
            {
                System.out.println("Error: Memory address " + address + " is beyond hardware limits (0x0000-0xFFFF)");
                return false;
            }
            return true;
        }
        catch (NumberFormatException e)
        {
            System.out.println("Error: Invalid hex value in address '" + address + "'");
            return false;
        }
    }

    private BigDecimal getValue(String operand)
    {
        try
        {
            if (operand.startsWith("#"))
            {
                return new BigDecimal(operand.substring(1));
            }
            else if (isValidAddress(operand))
            {
                return memory.getOrDefault(operand, BigDecimal.ZERO);
            }
            else
            {
                throw new IllegalArgumentException("Unexpected memory address: " + operand);
            }
        }
        catch (NumberFormatException e)
        {
            System.out.println("Error: Invalid number format '" + operand + "'");
            return BigDecimal.ZERO;
        }
        catch (IllegalArgumentException e)
        {
            System.out.println("Error: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    private void setValue(String address, BigDecimal value)
    {
        try
        {
            if (isValidAddress(address))
            {
                memory.put(address, value);
            }
            else
            {
                throw new IllegalArgumentException("Cannot access memory address: " + address);
            }
        }
        catch (IllegalArgumentException e)
        {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Memory Operations
    private void printMemory(String address)
    {
        if (debugMode)
        {
            System.out.println("Memory[" + address + "] = " + getValue(address));
        }
        else
        {
            System.out.println(getValue(address));
        }
    }

    private void loadImmediate(String address, String valueStr)
    {
        setValue(address, getValue(valueStr));
    }

    private void loadFromMemory(String dest, String src)
    {
        setValue(dest, getValue(src));
    }

    private void storeMemory(String src, String dest)
    {
        setValue(dest, getValue(src));
    }

    private void moveData(String dest, String src)
    {
        setValue(dest, getValue(src));
    }

    // Arithmetic Operations
    private void arithmeticOperation(String dest, String src, String operation)
    {
        try
        {
            BigDecimal val1 = getValue(dest);
            BigDecimal val2 = getValue(src);
            BigDecimal result = BigDecimal.ZERO;

            switch (operation)
            {
                case "ADD":
                    result = val1.add(val2);
                    break;
                case "SUB":
                    result = val1.subtract(val2);
                    break;
                case "MUL":
                    result = val1.multiply(val2);
                    break;
                case "DIV":
                    if (val2.compareTo(BigDecimal.ZERO) == 0)
                    {
                        System.out.println("Error: Division by zero");
                        result = BigDecimal.ZERO;
                    }
                    else
                    {
                        result = val1.divide(val2, 32, RoundingMode.HALF_UP);
                    }
                    break;
                case "MOD":
                    if (val2.compareTo(BigDecimal.ZERO) == 0)
                    {
                        System.out.println("Error: Modulo by zero");
                        result = BigDecimal.ZERO;
                    }
                    else
                    {
                        result = val1.remainder(val2);
                    }
                    break;
            }

            setValue(dest, result);
        }
        catch (Exception e)
        {
            System.out.println("Error in arithmetic operation: " + e.getMessage());
        }
    }

    private void increment(String address)
    {
        setValue(address, getValue(address).add(BigDecimal.ONE));
    }

    private void decrement(String address)
    {
        setValue(address, getValue(address).subtract(BigDecimal.ONE));
    }

    // Bitwise Operations
    private void bitwiseOperation(String dest, String src, String operation)
    {
        BigDecimal val1 = getValue(dest);
        BigDecimal val2 = getValue(src);

        BigInteger bigInt1 = val1.toBigInteger();
        BigInteger bigInt2 = val2.toBigInteger();
        BigInteger result = BigInteger.ZERO;

        switch (operation)
        {
            case "AND":
                result = bigInt1.and(bigInt2);
                break;
            case "OR":
                result = bigInt1.or(bigInt2);
                break;
            case "XOR":
                result = bigInt1.xor(bigInt2);
                break;
        }

        setValue(dest, new BigDecimal(result));
    }

    private void bitwiseNot(String address)
    {
        BigDecimal value = getValue(address);
        BigInteger bigValue = value.toBigInteger();
        setValue(address, new BigDecimal(bigValue.not()));
    }

    private void shiftOperation(String dest, String src, String direction)
    {
        BigDecimal value = getValue(dest);
        BigDecimal shift = getValue(src);

        BigInteger bigValue = value.toBigInteger();
        int shiftAmount = shift.intValue();

        if (direction.equals("LEFT"))
        {
            setValue(dest, new BigDecimal(bigValue.shiftLeft(shiftAmount)));
        }
        else
        {
            setValue(dest, new BigDecimal(bigValue.shiftRight(shiftAmount)));
        }
    }

    // Comparison
    private void compare(String addr1, String addr2)
    {
        BigDecimal val1 = getValue(addr1);
        BigDecimal val2 = getValue(addr2);
        comparisonResult = val1.compareTo(val2);
    }

    // Control Flow
    private void jump(String label)
    {
        if (labels.containsKey(label))
        {
            programCounter = labels.get(label);
        }
        else
        {
            throw new IllegalArgumentException("Unknown label: " + label);
        }
    }

    // POWER operation: dest = dest ^ src
    private void powerOperation(String dest, String src)
    {
        BigDecimal base = getValue(dest);
        BigDecimal exponent = getValue(src);
        
        if (exponent.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0)
        {
            // Integer exponent - use BigDecimal's pow() for perfect precision
            try
            {
                setValue(dest, base.pow(exponent.intValueExact()));
            }
            catch (ArithmeticException e)
            {
                System.out.println("Error: Exponent too large for integer power operation");
                setValue(dest, BigDecimal.ZERO);
            }
        }
        else
        {
            // Decimal exponent - use log/exp method for better precision
            try
            {
                // dest = e^(exponent * ln(base))
                BigDecimal result = BigDecimal.valueOf(
                    Math.exp(exponent.doubleValue() * Math.log(base.doubleValue()))
                );
                setValue(dest, result);
            }
            catch (Exception e)
            {
                System.out.println("Error in power operation: " + e.getMessage());
                setValue(dest, BigDecimal.ZERO);
            }
        }
    }

    // NEGATE operation: address = -address
    private void negateOperation(String address)
    {
        BigDecimal value = getValue(address);
        setValue(address, value.negate());
    }
    
    // ABSOLUTE operation: address = |address|
    private void absoluteOperation(String address)
    {
        BigDecimal value = getValue(address);
        setValue(address, value.abs());
    }
    private void jumpConditional(String label, String condition)
    {
        boolean shouldJump = false;

        switch (condition)
        {
            case "ZERO":
                shouldJump = comparisonResult == 0;
                break;
            case "NOT_ZERO":
                shouldJump = comparisonResult != 0;
                break;
            case "EQUAL":
                shouldJump = comparisonResult == 0;
                break;
            case "NOT_EQUAL":
                shouldJump = comparisonResult != 0;
                break;
            case "GREATER":
                shouldJump = comparisonResult > 0;
                break;
            case "LESS":
                shouldJump = comparisonResult < 0;
                break;
        }

        if (shouldJump)
        {
            jump(label);
        }
    }

    private void call(String label)
    {
        callStack.push(new BigDecimal(programCounter));
        jump(label);
    }

    private void returnFromCall()
    {
        if (!callStack.isEmpty())
        {
            programCounter = callStack.pop().intValue();
        }
        else
        {
            running = false;
        }
    }

    private void halt()
    {
        running = false;
    }

    // Stack Operations
    private void push(String address)
    {
        callStack.push(getValue(address));
    }

    private void pop(String address)
    {
        try
        {
            if (!callStack.isEmpty())
            {
                setValue(address, callStack.pop());
            }
            else
            {
                System.out.println("Error: Stack underflow - cannot pop from empty stack");
            }
        }
        catch (Exception e)
        {
            System.out.println("Error in pop operation: " + e.getMessage());
        }
    }

    private void pushAll()
    {
        memory.keySet().stream()
                .sorted()
                .forEach(key -> callStack.push(memory.get(key)));
    }

    private void popAll()
    {
        memory.keySet().stream()
                .sorted()
                .forEach(key ->
                {
                    if (!callStack.isEmpty())
                    {
                        setValue(key, callStack.pop());
                    }
                });
    }

    // I/O Operations
    private void input(String address)
    {
        Scanner scanner = new Scanner(System.in);
        try
        {
            BigDecimal inputValue = new BigDecimal(scanner.nextLine());
            setValue(address, inputValue);
        }
        catch (NumberFormatException e)
        {
            System.out.println("Error: Invalid number format. Please enter a valid number.");
        }
    }

    private void printChar(String address)
    {
        BigDecimal value = getValue(address);
        int charValue = value.intValue();
        System.out.print((char) charValue);
    }

    private void printString(String startAddress)
    {
        try
        {
            int i = 0;
            while (true)
            {
                String currentAddr;
                if (startAddress.startsWith("0x"))
                {
                    int baseAddr = Integer.parseInt(startAddress.substring(2), 16);
                    currentAddr = String.format("0x%04X", baseAddr + i);

                    if (!isValidAddress(currentAddr))
                    {
                        System.out.println("Error: String access beyond memory bounds at " + currentAddr);
                        break;
                    }

                    BigDecimal value = getValue(currentAddr);
                    if (value.compareTo(BigDecimal.ZERO) == 0)
                    {
                        break;
                    }

                    int charValue = value.intValue();
                    System.out.print((char) charValue);
                    i++;

                    if (i > 1000)
                    {
                        System.out.println("Error: String too long or missing null terminator");
                        break;
                    }
                }
                else
                {
                    System.out.println("Error: Invalid string starting address '" + startAddress + "'");
                    break;
                }
            }
        }
        catch (NumberFormatException e)
        {
            System.out.println("Error: Invalid address in printString '" + startAddress + "'");
        }
        catch (Exception e)
        {
            System.out.println("Error in printString: " + e.getMessage());
        }
    }

    // Special Operations
    private void random(String address)
    {
        BigDecimal randomValue = new BigDecimal(Math.random() * 100);
        setValue(address, randomValue);
    }

    private void swap(String addr1, String addr2)
    {
        BigDecimal temp = getValue(addr1);
        setValue(addr1, getValue(addr2));
        setValue(addr2, temp);
    }

    private void clear(String address)
    {
        setValue(address, BigDecimal.ZERO);
    }

    private void dumpMemory()
    {
        if (debugMode)
        {
            System.out.println("\n=== Memory Dump ===");
            memory.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
            System.out.println("==================\n");
        }
        // In normal mode, do nothing (no output)
    }

    public static void main(String[] args)
    {
        Tonnyi esolang = new Tonnyi();

        // Check if filename was provided as command line argument
        if (args.length > 0)
        {
            String filename = args[0];
            // Ensure it has .ton extension
            if (!filename.toLowerCase().endsWith(".ton"))
            {
                filename = filename + ".ton";
            }

            try
            {
                esolang.executeFromFile(filename);
            }
            catch (Exception e)
            {
                System.out.println("Error executing program: " + e.getMessage());
                System.out.println("File not found: " + filename);
            }
        }
        else
        {
            // Show instruction set and information
            printHelp();
        }
    }

    private static void printHelp()
    {
        System.out.println("=== Tonnyi ===");
        System.out.println("7-bit instruction set with 128 possible operations");
        System.out.println();
        System.out.println("USAGE:");
        System.out.println("  java Tonnyi <filename.ton>");
        System.out.println();
        System.out.println("EXAMPLE:");
        System.out.println("  java Tonnyi program.ton");
        System.out.println("  java Tonnyi calculator");
        System.out.println();
        System.out.println("INSTRUCTION SET (44 instructions):");
        System.out.println();
        System.out.println("SYSTEM OPERATIONS (0-2):");
        System.out.println("  0b0000000  HALT");
        System.out.println("  0b0000001  NOP");
        System.out.println("  0b0000010  DUMP MEMORY (debug mode only)");
        System.out.println();
        System.out.println("MEMORY OPERATIONS (3-9):");
        System.out.println("  0b0000011  PRINT <address>");
        System.out.println("  0b0000100  LOAD IMMEDIATE <dest>, <value>");
        System.out.println("  0b0000101  LOAD FROM MEMORY <dest>, <src>");
        System.out.println("  0b0000110  MOV <dest>, <src>");
        System.out.println("  0b0000111  STORE <src>, <dest>");
        System.out.println("  0b0001000  SWAP <addr1>, <addr2>");
        System.out.println("  0b0001001  CLEAR <address>");
        System.out.println();
        System.out.println("ARITHMETIC OPERATIONS (10-19):");
        System.out.println("  0b0001010  ADD <dest>, <src>");
        System.out.println("  0b0001011  SUBTRACT <dest>, <src>");
        System.out.println("  0b0001100  MULTIPLY <dest>, <src>");
        System.out.println("  0b0001101  DIVIDE <dest>, <src>");
        System.out.println("  0b0001110  MODULO <dest>, <src>");
        System.out.println("  0b0001111  INCREMENT <address>");
        System.out.println("  0b0010000  DECREMENT <address>");
        System.out.println("  0b0010001  POWER <dest>, <src>");
        System.out.println("  0b0010010  NEGATE <address>");
        System.out.println("  0b0010011  ABSOLUTE <address>");
        System.out.println();
        System.out.println("BITWISE OPERATIONS (20-25):");
        System.out.println("  0b0010100  AND <dest>, <src>");
        System.out.println("  0b0010101  OR <dest>, <src>");
        System.out.println("  0b0010110  XOR <dest>, <src>");
        System.out.println("  0b0010111  NOT <address>");
        System.out.println("  0b0011000  SHIFT LEFT <dest>, <shift>");
        System.out.println("  0b0011001  SHIFT RIGHT <dest>, <shift>");
        System.out.println();
        System.out.println("COMPARISON OPERATIONS (26):");
        System.out.println("  0b0011010  COMPARE <addr1>, <addr2>");
        System.out.println();
        System.out.println("CONTROL FLOW OPERATIONS (27-35):");
        System.out.println("  0b0011011  JUMP <label>");
        System.out.println("  0b0011100  JUMP IF ZERO <label>");
        System.out.println("  0b0011101  JUMP IF NOT ZERO <label>");
        System.out.println("  0b0011110  JUMP IF EQUAL <label>");
        System.out.println("  0b0011111  JUMP IF NOT EQUAL <label>");
        System.out.println("  0b0100000  JUMP IF GREATER <label>");
        System.out.println("  0b0100001  JUMP IF LESS <label>");
        System.out.println("  0b0100010  CALL <label>");
        System.out.println("  0b0100011  RETURN");
        System.out.println();
        System.out.println("STACK OPERATIONS (36-37):");
        System.out.println("  0b0100100  PUSH <address>");
        System.out.println("  0b0100101  POP <address>");
        System.out.println();
        System.out.println("I/O OPERATIONS (38-40):");
        System.out.println("  0b0100110  INPUT <address>");
        System.out.println("  0b0100111  PRINT CHAR <address>");
        System.out.println("  0b0101000  PRINT STRING <start_address>");
        System.out.println();
        System.out.println("SPECIAL OPERATIONS (41):");
        System.out.println("  0b0101001  RANDOM <address>");
        System.out.println();
        System.out.println("DEBUG OPERATIONS (42-43):");
        System.out.println("  0b0101010  DEBUG MODE ON");
        System.out.println("  0b0101011  DEBUG MODE OFF");
        System.out.println();
        System.out.println("MEMORY ADDRESSING:");
        System.out.println("  Use 4-digit hex addresses: 0x0000 to 0xFFFF");
        System.out.println("  Immediate values: #123, #3.14, #-42");
        System.out.println();
        System.out.println("DEBUG FEATURES:");
        System.out.println("  Use 0b0101010 to enable debug mode");
        System.out.println("  Use 0b0101011 to disable debug mode");
        System.out.println("  PRINT shows Memory[address] = value in debug mode");
        System.out.println("  DUMP MEMORY only outputs in debug mode");
        System.out.println();
        System.out.println("BINARY-DECIMAL MAPPING:");
        System.out.println("  Each opcode's binary value equals its decimal position");
        System.out.println("  Example: 0b0001101 (binary 13) = DIVIDE (13th instruction)");
        System.out.println();
        System.out.println("Create .ton files with your favorite text editor!");
    }
}