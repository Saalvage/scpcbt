Type Keys
	Field RIGHT%, LEFT%, UP%, DOWN%
	Field BLINK%, SPRINT%, INV%, CROUCH%, SAVE%, CONSOLE%
	Field KeyName$[211]
End Type

Local I_Keys.Keys = New Keys

I_Keys\RIGHT = GetINIInt(OptionFile, "binds", "Right key")
I_Keys\LEFT = GetINIInt(OptionFile, "binds", "Left key")
I_Keys\UP = GetINIInt(OptionFile, "binds", "Up key")
I_Keys\DOWN = GetINIInt(OptionFile, "binds", "Down key")

I_Keys\BLINK = GetINIInt(OptionFile, "binds", "Blink key")
I_Keys\SPRINT = GetINIInt(OptionFile, "binds", "Sprint key")
I_Keys\INV = GetINIInt(OptionFile, "binds", "Inventory key")
I_Keys\CROUCH = GetINIInt(OptionFile, "binds", "Crouch key")
I_Keys\SAVE = GetINIInt(OptionFile, "binds", "Save key")
I_Keys\CONSOLE = GetINIInt(OptionFile, "binds", "Console key")

I_Keys\KeyName[1]="Esc"
For i = 2 To 10
	I_Keys\KeyName[i]=i-1
Next
I_Keys\KeyName[11]="0"
I_Keys\KeyName[12]="-"
I_Keys\KeyName[13]="="
I_Keys\KeyName[14]=GetLocalString("Keys", "backspace")
I_Keys\KeyName[15]=GetLocalString("Keys", "tab")
I_Keys\KeyName[16]="Q"
I_Keys\KeyName[17]="W"
I_Keys\KeyName[18]="E"
I_Keys\KeyName[19]="R"
I_Keys\KeyName[20]="T"
I_Keys\KeyName[21]="Y"
I_Keys\KeyName[22]="U"
I_Keys\KeyName[23]="I"
I_Keys\KeyName[24]="O"
I_Keys\KeyName[25]="P"
I_Keys\KeyName[26]="["
I_Keys\KeyName[27]="]"
I_Keys\KeyName[28]=GetLocalString("Keys", "enter")
I_Keys\KeyName[29]=GetLocalString("Keys", "lctrl")
I_Keys\KeyName[30]="A"
I_Keys\KeyName[31]="S"
I_Keys\KeyName[32]="D"
I_Keys\KeyName[33]="F"
I_Keys\KeyName[34]="G"
I_Keys\KeyName[35]="H"
I_Keys\KeyName[36]="J"
I_Keys\KeyName[37]="K"
I_Keys\KeyName[38]="L"
I_Keys\KeyName[39]=";"
I_Keys\KeyName[40]="'"
I_Keys\KeyName[42]=GetLocalString("Keys", "lshift")
I_Keys\KeyName[43]="\"
I_Keys\KeyName[44]="Z"
I_Keys\KeyName[45]="X"
I_Keys\KeyName[46]="C"
I_Keys\KeyName[47]="V"
I_Keys\KeyName[48]="B"
I_Keys\KeyName[49]="N"
I_Keys\KeyName[50]="M"
I_Keys\KeyName[51]=","
I_Keys\KeyName[52]="."
I_Keys\KeyName[54]=GetLocalString("Keys", "rshift")
I_Keys\KeyName[56]=GetLocalString("Keys", "lalt")
I_Keys\KeyName[57]=GetLocalString("Keys", "space")
I_Keys\KeyName[58]=GetLocalString("Keys", "capslock")
I_Keys\KeyName[59]="F1"
I_Keys\KeyName[60]="F2"
I_Keys\KeyName[61]="F3"
I_Keys\KeyName[62]="F4"
I_Keys\KeyName[63]="F5"
I_Keys\KeyName[64]="F6"
I_Keys\KeyName[65]="F7"
I_Keys\KeyName[66]="F8"
I_Keys\KeyName[67]="F9"
I_Keys\KeyName[68]="F10"
I_Keys\KeyName[157]=GetLocalString("Keys", "rctrl")
I_Keys\KeyName[184]=GetLocalString("Keys", "ralt")
I_Keys\KeyName[200]=GetLocalString("Keys", "up")
I_Keys\KeyName[203]=GetLocalString("Keys", "left")
I_Keys\KeyName[205]=GetLocalString("Keys", "right")
I_Keys\KeyName[208]=GetLocalString("Keys", "down")

