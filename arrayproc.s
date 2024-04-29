    .section .data
idprstr: .asciz "Enter your TCU ID number: "
        .align 2
idstr:   .asciz "%d" 
        .align 2
avgstr: .asciz "The average of the duplicate array is: %d\n"
        .align 2
arrstr: .asciz "%5d\t"
        .align 2
newline: .asciz "\n"
        .align 2
tab:     .asciz "\t"
        .align 2
initstr: .asciz "\nCreating two arrays of %d values each\n"
        .align 2
origstr: .asciz "\nThe original array is:\n"
        .align 2
dupestr: .asciz "\nThe sorted duplicate array is:\n"
        .align 2

    .section .bss 
tcuID:  .skip 4

    .section .text
    .global main

main:
    /*=============================/*
    |          PROLOG CODE          |
    /*=============================*/

    stp	x29, x30, [sp, #-16]!	// this saves X29 and X30 onto the stack
	mov	x29, sp			// this moves the SP to the FP

    //generate random seed for rand()
    mov x0, #0
    bl time
    bl srand

   // printf("Enter your TCU ID number: ")
    adr x0, idprstr
    bl printf

   // scanf("%d", &tcuID)
    adr x0, idstr
    adr x1, tcuID
    bl scanf


    // Take tcuID, check parity, depending on parity allocate either 53-el if odd or 56-el if even
    adr x0, tcuID
    ldr x1, [x0]
    and x0, x1, #1
    cmp x0, #0 // if tcuID % 2 == 0, then EVEN
    beq even
   
    //put n into x21
    //n = 53 if odd
    mov x21, #53

    b callstart

even: 
    //put n into x21
    // n = 56 if odd
    mov x21, #56

callstart:
    //store x19, x20 onto stack so that they can hold pointers to orig and dupe, and store x21 so that it can hold n for later
    sub sp, sp, #32 
    str x21, [sp, #8] //n
    str x20, [sp, #16] //*dupe[]
    str x19, [sp, #24] //*orig[]

    //store start of orig in x19
    mov x19, sp
    //allocate space for orig
    sub sp, sp, #256
    //store start of dupe in x19
    mov x20, sp
    //allocate space for dupe
    sub sp, sp, #256

    //printf("\nCreating two arrays of %d values each\n", x21)
    adr x0, initstr
    mov x1, x21
    bl printf

    //init_array(int *orig, int n)
    mov x0, x19
    mov x1, x21
    bl init_array

    //printf("\nThe original array is:n\")
    adr x0, origstr
    bl printf

    //print_array(int *orig, int n)
    mov x0, x19
    mov x1, x21
    bl print_array

    //copy_array(int *dupe, int *orig, int n)
    mov x0, x20
    mov x1, x19
    mov x2, x21
    bl copy_array


    //selection_sort(int *dupe, int n)
    mov x0, x20
    mov x1, x21
    bl selection_sort

    //printf("\nThe sorted duplicate array is:\n")
    adr x0, dupestr
    bl printf

    //print_array(int *dupe, int n)
    mov x0, x20
    mov x1, x21
    bl print_array

    //compute_average(int *dupe, int n)
    mov x0, x20
    mov x1, x21
    bl compute_average

    //average value is in x0

    //printf("\nThe average value of the duplicate array is: %d\n", x0)
    mov x1, x0
    adr x0, avgstr
    bl printf

    /*=============================/*
    |          EPILOG CODE          |
    /*=============================*/

    //reclaim stack space used for array 
    add sp, sp, #512

    //revert x19-x21 & reclaim stack space
    ldr x21, [sp, #8]
    ldr x20, [sp, #16]
    ldr x19, [sp, #24]
    add sp, sp, #32

    mov	x0, #0			    // setting return value to 0
	ldp	x29, x30, [sp], #16	// restore the X29 and X30 from the stack
	ret

    .global init_array
    .type init_array, %function
    // void init_array(int arr[], int n)
init_array:
    //arr[] in stack, n is # of 4-byte integers in array
    //pointer to arr[] in x0, n in x1

    //prolog
    sub sp, sp, #32
    str x30, [sp]
    str x22, [sp, #8]   //x22 will be used to traverse list
    str x23, [sp, #16]  //x23 will hold i
    
    mov x22, x0     //x22 holds *arr[]
    mov x23, #0     //i = 0

ialoop:
        cmp x23, x21 //i cmp n
        bge iaendloop //if i>=n b end

        //rand()
        bl rand
        and x0, x0, #255    //x0%256
        str w0, [x22]       //store 4 bytes into *x22
        sub x22, x22, #4    //*arr[i]=>*arr[i+1]
        add x23, x23, #1    //i++
        b ialoop

iaendloop:

    //epilog
    ldr x23, [sp, #16]
    ldr x22, [sp, #8]
    ldr x30, [sp]
    add sp, sp, #32
    ret

    .global print_array
    .type print_array, %function
    // void print_array(int arr[], int n)
print_array:
    //arr[] in stack, n is # of 4-byte integers in array
    //pointer to arr[] in x0, n in x1

    //prolog
    sub sp, sp, #32
    str x30, [sp]
    str x22, [sp, #8]
    str x23, [sp, #16]

    mov x22, x0     //x22 holds *arr[]
    mov x23, #0     //i = 0

paloop:
    cmp x23, x21    //i cmp n
    bge paendloop

    //printf("%5d\t", arr[i])
    adr x0, arrstr
    ldr w1, [x22]
    bl printf

    //modulo(i+1, 5)
    add x0, x23, #1 //i+1 in x0
    mov x1, #5      // 5 in x1
    bl modulo

    //i+1 mod 5 in x0
    mov x1, #0
    cmp x0, x1
    bne nonew   //if !=0, dont print \n

    adr x0, newline 
    bl printf

nonew:

    sub x22, x22, #4    //*arr[i] => *arr[i+1]
    add x23, x23, #1    //i++
    b paloop

paendloop:
    //printf("\n") after last element has been printed
    adr x0, newline 
    bl printf

    //epilog
    ldr x23, [sp, #16]
    ldr x22, [sp, #8]
    ldr x30, [sp]
    add sp, sp, #32
    ret

    .global copy_array
    .type copy_array, %function
    // void copy_array(int dest[], int src[], int n)
copy_array:
    //dest[], src[] in stack, n is # of 4-byte integers in array
    //pointer to dest[] in x0, pointer to src[] in x1, n in x2

    //prolog
    sub sp, sp, #16
    str x30, [sp]
    str x22, [sp, #8]   //i will be in x22
   
    // *dest[] in x0, *src[] in x1, n in x21
    mov x22, #0 //i = 0

caloop:
    cmp x22, x21
    bge caendloop   //if i>=n branch

    ldr w3, [x1]    //src[i] => x3
    str w3, [x0]    //x3 => dest[i]
    sub x0, x0, #4      //dest[i] => dest[i+1]
    sub x1, x1, #4      //src[i] => src[i+1]
    add x22, x22, #1    //i++

    b caloop

caendloop:

    //epilog
    ldr x22, [sp, #8]
    ldr x30, [sp]
    add sp, sp, #16
    ret

    .global swap
    .type swap, %function
    // void swap(int *a, int *b)
swap:
    //*a, *b are pointers to integers in memory
    //*a in x0, *b in x1

    //prolog
    sub sp, sp, #16
    str x22, [sp]         //x22 is tempA
    str x23, [sp, #8]     //x23 is tempB

    ldr w22, [x0]   //&a => int tempA
    ldr w23, [x1]   //&b => int tempB

    str w23, [x0]   //put tempB into a
    str w22, [x1]   //put tempA into b

    //epilog
    ldr x23, [sp, #8]
    ldr x22, [sp]
    add sp, sp, #16
    ret

    .global selection_sort
    .type selection_sort, %function
    // void selection_sort(int arr[], int n)
selection_sort:
    //arr[] in stack, n is # of 4-byte integers in array
    //pointer to arr[] in x0, n in x1 (and x21)
    //locals x22 is arr[] for outer loop, x23 is arr[] for inner loop, x24 for i, x25 for min, x26 for j

    //prolog
    sub sp, sp, #64
    str x30, [sp]
    str x22, [sp, #8]  //pointer to start of arr[]
    str x23, [sp, #16] //temp array pointer
    str x24, [sp, #24] //temp array pointer
    str x25, [sp, #32] //step
    str x26, [sp, #40] //min
    str x27, [sp, #48] //i
    str x28, [sp, #46] //n+1

    mov x22, x0 //x22 holds pointer to start of arr[]
    add x28, x21, #1

    mov x25, #0 //step = 0
ssloop1:
   //if(step>=n) b ssendloop1
   cmp x25, x21
   bge ssendloop1

   mov x26, x25 //min = step
   add x27, x25, #1 //i = step+1
ssloop2:
    //if(i>=n) b ssendloop2
    cmp x27, x28
    bge ssendloop2

    //if(arr[i]>=arr[min]) b ssendif
    //arr[x] = [x22-x*4]
    mov x0, #4
    mul x0, x27, x0 //i*4
    sub x23, x22, x0 //*arr[i] => x23
    mov x0, #4
    mul x0, x26, x0 //min*4
    sub x24, x22, x0 //*arr[min] => x24
    ldr w0, [x23] //&arr[i] => w0
    ldr w1, [x24] //&arr[min] => w1
    cmp w0, w1
    bge ssendif

    mov x26, x27 //min = i
ssendif:
    add x27, x27, #1 //i++
    b ssloop2
ssendloop2:
    //swap(*arr[min], *arr[step])
    mov x0, #4
    mul x0, x25, x0
    sub x1, x22, x0 //*arr[step] => x1
    mov x0, x24 //*arr[min] => x0
    bl swap

    add x25, x25, #1 //step++
    b ssloop1
ssendloop1:

    //epilog
    ldr x28, [sp, #56]
    ldr x27, [sp, #48]
    ldr x26, [sp, #40]
    ldr x25, [sp, #32]
    ldr x24, [sp, #24]
    ldr x23, [sp, #16]
    ldr x22, [sp, #8]
    ldr x30, [sp]
    add sp, sp, #64
    ret

    .global compute_average
    .type compute_average, %function
    // int compute_average(int arr[], int n)
compute_average:
    //arr[] in stack, n is # of 4-byte integers in array
    //pointer to arr[] in x0, n in x1

    //prolog
    sub sp, sp, #16
    str x30, [sp]
  

    //sum_array(int arr[], int startidx, int stopidx)
    //x0 already has arr[]
    mov x1, #0
    mov x2, x21
    bl sum_array

    //sum is in x0, get avg and put in x0 to return
    //avg = sum / n
    udiv x0, x0, x21 

    //epilog

    ldr x30, [sp]
    add sp, sp, #16
    ret

    .global sum_array
    .type sum_array, %function
    //int sum_array(int *arr[], int startidx, int stopidx)
sum_array:
    //arr[] is in stack, startidx is starting index, stopidx is ending index
    //pointer to arr[] in x0, startidx in x1, stopidx in x2

    //prolog
    sub sp, sp, #32
    str x30, [sp]
    str x22, [sp, #8] //temp array pointer
    str x23, [sp, #16] //i 
    str x24, [sp, 24] //temp value reg

    //if startidx>stopidx b sabasecase
    cmp x1, x2
    bge sabasecase

    //else return arr[startidx] + sum_array(*arr[], startidx+1, stopidx)
    //arr[startidx]
    mov x23, #4
    mul x23, x1, x23 //startidx*4 => x23 == i
    sub x22, x0, x23 //*arr[i] => x22
    ldr w24, [x22]   //&arr[i] => w24

    //put w24 onto stack so it's value is not overwritten during recursive call
    sub sp, sp, #16
    str w24, [sp]


    //sum_array(*arr[], startidx+1, stopidx)
    //x0 already holds *arr[], and x2 already holds stopidx
    add x1, x1, #1 //startidx+1 => x1
    bl sum_array

    //restore w24 before calculation
    ldr w24, [sp]
    add sp, sp, #16

    //return value 
    add w0, w24, w0 //return  arr[startidx] + sum_array(*arr[], startidx+1, stopidx)
    //restore registers and reclaim stack space
    ldr x24, [sp, #24]
    ldr x23, [sp, #16]
    ldr x22, [sp, #8]
    ldr x30, [sp]
    add sp, sp, #32
    ret

sabasecase:
    //return 0 
    mov w0, #0
    //restore registers and reclaim stack space
    ldr x24, [sp, #24]
    ldr x23, [sp, #16]
    ldr x22, [sp, #8]
    ldr x30, [sp]
    add sp, sp, #32
    ret

    .global modulo
    .type modulo, %function
    //int modulo(int x, int y)
modulo:
    //x in x0, y in x1
    //does subtraction modulo method to emulate x mod y, returns remainder in x0

    cmp x0, x1
    blt endmod

    sub x0, x0, x1
    b modulo

endmod:
    ret
