rule meterpreter_reverse_tcp_shellcode {
    meta:
        author = "FDD @ Cuckoo sandbox"
        description = "Rule for metasploit's  meterpreter reverse tcp raw shellcode"

    strings:
        $s1 = { fce8 8?00 0000 60 }     // shellcode prologe in metasploit
        $s2 = { 648b ??30 }             // mov edx, fs:[???+0x30]
        $s3 = { 4c77 2607 }             // kernel32 checksum
        $s4 = "ws2_"                    // ws2_32.dll
        $s5 = { 2980 6b00 }             // WSAStartUp checksum
        $s6 = { ea0f dfe0 }             // WSASocket checksum
        $s7 = { 99a5 7461 }             // connect checksum

    condition:
        all of them and filesize < 5KB
}

rule meterpreter_reverse_tcp_shellcode_rev1 {
    meta:
        author = "FDD @ Cuckoo sandbox"
        description = "Meterpreter reverse TCP shell rev1"
        LHOST = 0xae
        LPORT = 0xb5

    strings:
        $s1 = { 6a00 53ff d5 }

    condition:
        meterpreter_reverse_tcp_shellcode and $s1 in (270..filesize)
}

rule meterpreter_reverse_tcp_shellcode_rev2 {
    meta:
        author = "FDD @ Cuckoo sandbox"
        description = "Meterpreter reverse TCP shell rev2"
        LHOST = 194
        LPORT = 201

    strings:
        $s1 = { 75ec c3 }

    condition:
        meterpreter_reverse_tcp_shellcode and $s1 in (270..filesize)
}

rule meterpreter_reverse_tcp_shellcode_domain {
    meta:
        author = "FDD @ Cuckoo sandbox"
        description = "Variant used if the user specifies a domain instead of a hard-coded IP"

    strings:
        $s1 = { a928 3480 }             // Checksum for gethostbyname
        $domain = /(\w+\.)+\w{2,6}/

    condition:
        meterpreter_reverse_tcp_shellcode and all of them
}

rule metasploit_download_exec_shellcode_rev1 {
    meta:
        author = "FDD @ Cuckoo Sandbox"
        description = "Rule for metasploit's download and exec shellcode"
        name = "Metasploit download & exec payload"
        URL = 185

    strings:
        $s1 = { fce8 8?00 0000 60 }     // shellcode prologe in metasploit
        $s2 = { 648b ??30 }             // mov edx, fs:[???+0x30]
        $s4 = { 4c77 2607 }             // checksum for LoadLibraryA
        $s5 = { 3a56 79a7 }             // checksum for InternetOpenA
        $s6 = { 5789 9fc6 }             // checksum for InternetConnectA
        $s7 = { eb55 2e3b }             // checksum for HTTPOpenRequestA
        $s8 = { 7546 9e86 }             // checksum for InternetSetOptionA
        $s9 = { 2d06 187b }             // checksum for HTTPSendRequestA
        $url = /\/[\w_\-\.]+/

    condition:
        all of them and filesize < 5KB
}

rule metasploit_download_exec_shellcode_rev2 {
    meta:
        author = "FDD @ Cuckoo Sandbox"
        description = "Rule for metasploit's download and exec shellcode"
        name = "Metasploit download & exec payload"
        URL = 185

    strings:
        $s1 = { fce8 8?00 0000 60 }     // shellcode prologe in metasploit
        $s2 = { 648b ??30 }             // mov edx, fs:[???+0x30]
        $s4 = { 4c77 2607 }             // checksum for LoadLibraryA
        $s5 = { 3a56 79a7 }             // checksum for InternetOpenA
        $s6 = { 5789 9fc6 }             // checksum for InternetConnectA
        $s7 = { eb55 2e3b }             // checksum for HTTPOpenRequestA
        $s9 = { 2d06 187b }             // checksum for HTTPSendRequestA
        $url = /\/[\w_\-\.]+/

    condition:
        all of them and filesize < 5KB
}

rule metasploit_bind_shell {
    meta:
        author = "FDD @ Cuckoo Sandbox"
        description = "Rule for metasploit's bind shell shellcode"
        name = "Metasploit bind shell payload"

    strings:
        $s1 = { fce8 8?00 0000 60 }     // shellcode prologe in metasploit
        $s2 = { 648b ??30 }             // mov edx, fs:[???+0x30]
        $s3 = { 4c77 2607 }             // checksum for LoadLibraryA
        $s4 = { 2980 6b00 }             // checksum for WSAStartup
        $s5 = { ea0f dfe0 }             // checksum for WSASocketA
        $s6 = { c2db 3767 }             // checksum for bind
        $s7 = { b7e9 38ff }             // checksum for listen
        $s8 = { 74ec 3be1 }             // checksum for accept

    condition:
        all of them and filesize < 5KB
}




rule metasploit_xor_shell {
    meta:
        author = "Guli"

    strings:
        $s1 = { 4831 c9 }               // xor rcx, rcx
        $s2 = { 4881 }                  // sub ecx, block count
        $s3 = { 488d 05ef ffff ff }     // lea rax, [rel 0x0]
        $s4 = { 48bb ???? ???? }        // mov rbx, 0x????????????????
        $s5 = { 4831 5827 }             // xor [rax+0x27], rbx
        $s6 = { 482d f8ff ffff }        // sub rax, -8
        $s7 = { e2f4 }                  // loop 0x1B

    condition:
        all of them and filesize < 5KB
}
rule metasploit_xor_meterpreter {
    meta:
        author = "Guli"

    strings:
        $s1 = { 4831 c9 }               // xor rcx, rcx
        $s2 = { 4881 }                  // sub ecx, block count
        $s3 = { 488d 05ef ffff ff }     // lea rax, [rel 0x0]
        $s4 = { 48bb ???? ???? }        // mov rbx, 0x????????????????
        $s5 = { 4831 5827 }             // xor [rax+0x27], rbx
        $s6 = { 482d f8ff ffff }        // sub rax, -8
        $s7 = { e2f4 }                  // loop 0x1B

    condition:
        all of them and filesize > 5KB
}




rule metasploit_or_shell_alpha_mixed {

    meta:
        author = "Guli"

    strings:
        $start1 = { 89 }
        $start2 = { DB }
        $padding1 = {49 43}
        $unique = { D9 }

    condition:
        ($start1 at 0 or $start2 at 0) and $padding1 and $unique
}

rule metasploit_or_shell_alpha_upper {

    meta:
        author = "Guli"

    strings:
        $start1 = { 89 }
        $start2 = { DA }
        $padding1 = { 59 49 }
        $padding2 = { 4A 43 }
        $s3 = { 43 43 43 43 43 43 51 5A 56 54 58 33 30 56 58 34 41 50 30 41 33 48 48 30 41 30 30 41 42 41 41 42 54 41 41 51 32 41 42 32 42 42 30 42 42 58 50 38 41 43 4A 4A 49 4B 4C }

    condition:
        ($start1 at 0 or $start2 at 0) and ($padding1 or $padding2) or $s3
}

rule metasploit_or_shell_call_four_dword {

    meta:
        author = "Guli"

    strings:
        $s1 = { C9 83E9 AAE8 FFFF FFFF C05E 8176 0E }

    condition:
        all of them
}

rule metasploit_or_shell_fnstenv_mov {

    meta:
        author = "Guli"

    strings:
        $s1 = { 6A 56 59 D9 EE D9 74 24 F4 5B 81 73 13 }
        $s2 = { 83 EB FC E2 F4 }

    condition:
        all of them
}


rule metasploit_or_shell_jmp_call_additive {

    meta:
        author = "Guli"

    strings:
        $s1 = { FC BB }
        $s2 = { EB 0C 5E 56 31 1E AD 01 C3 85 C0 75 F7 C3 E8 EF FF FF FF }

    condition:
        all of them
}

rule metasploit_or_shell_xor_dynamic {

    meta:
        author = "Guli"

    strings:
        $s1 = { EB 27 5B 53 5F B0 }
        $s2 = { FC AE 75 FD 57 59 53 5E 8A 06 30 07 48 FF C7 48 FF C6 66 81 3F }

    condition:
        all of them
}

rule metasploit_or_shell_shikata {

    meta:
        author = "Guli"

    strings:
        $s1 = { D9 74 24 F4 }
        $s2 = { 2B C9 B1 56 }
        $s4 = { 03 }

    condition:
        all of them
}