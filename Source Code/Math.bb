Function GenerateSeedNumber(Seed$)
	Local i%
 	Local Temp% = 0
 	Local Shift% = 0
	
 	For i = 1 To Len(Seed)
 		Temp = Temp Xor((Asc(Mid(Seed, i, 1)) Shl(Shift)))
 		Shift = (Shift + 1) Mod 24
	Next
 	Return(Temp)
End Function

Function CurveValue#(Number#, Old#, Smooth#)
	If FPSfactor = 0 Then Return(Old)
	
	If Number < Old Then
		Return(Max(Old + (Number - Old) * (1.0 / Smooth * FPSfactor), Number))
	Else
		Return(Min(Old + (Number - Old) * (1.0 / Smooth * FPSfactor), Number))
	EndIf
End Function

Function CurveAngle#(Val#, Old#, Smooth#)
	If FPSfactor = 0.0 Then Return(Old)
	
	Local Diff# = WrapAngle(Val) - WrapAngle(Old)
	
	If Diff > 180.0 Then Diff = Diff - 360.0
	If Diff < -180.0 Then Diff = Diff + 360.0
	
	Return(WrapAngle(Old + Diff * (1.0 / Smooth * FPSfactor)))
End Function

; This is necessary because negative numbers modulod yield negative numbers, thanks for your help Juan! :)
Function WrapAngle#(Angle#)
	If Angle = INFINITY Then Return(0.0)
	If Angle < 0.0 Then
		Return(360.0 + (Angle Mod 360.0))
	Else
		Return(Angle Mod 360.0)
	EndIf
End Function

Function Point_Direction#(x1#, z1#, x2#, z2#)
	Local dx#, dz#
	
	dx = x1 - x2
	dz = z1 - z2
	Return(ATan2(dz, dx))
End Function

Function AngleDist#(a0#, a1#)
	Local b# = a0 - a1
	Local bb#
	
	If b < -180.0 Then
		bb = b + 360.0
	ElseIf b > 180.0 Then
		bb = b - 360.0
	Else
		bb = b
	EndIf
	Return(bb)
End Function

Function f2s$(n#, Count%)
	Return(Left(n, Len(Int(n)) + Count + 1))
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D