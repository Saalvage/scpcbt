Function GenerateSeedNumber(seed$)
 	Local temp% = 0
 	Local shift% = 0
 	For i = 1 To Len(seed)
 		temp = temp Xor (Asc(Mid(seed,i,1)) Shl shift)
 		shift=(shift+1) Mod 24
	Next
 	Return temp
End Function

Function CurveValue#(number#, old#, smooth#)
	If FPSfactor = 0 Then Return old
	
	If number < old Then
		Return Max(old + (number - old) * (1.0 / smooth * FPSfactor), number)
	Else
		Return Min(old + (number - old) * (1.0 / smooth * FPSfactor), number)
	EndIf
End Function

Function CurveAngle#(val#, old#, smooth#)
	If FPSfactor = 0 Then Return old
	
   Local diff# = WrapAngle(val) - WrapAngle(old)
   If diff > 180 Then diff = diff - 360
   If diff < - 180 Then diff = diff + 360
   Return WrapAngle(old + diff * (1.0 / smooth * FPSfactor))
End Function

; This is necessary because negative numbers modulod yield negative numbers, thanks for your help Juan! :)
Function WrapAngle#(angle#)
	If angle = INFINITY Then Return 0.0
	If angle < 0 Then
		Return 360 + (angle Mod 360)
	Else
		Return angle Mod 360
	EndIf
End Function

Function GetAngle#(x1#, y1#, x2#, y2#)
	Return ATan2( y2 - y1, x2 - x1 )
End Function

Function CircleToLineSegIsect% (cx#, cy#, r#, l1x#, l1y#, l2x#, l2y#)
	
	;Palauttaa:
	;  True (1) kun:
	;	  Ympyrä [keskipiste = (cx, cy): säde = r]
	;	  leikkaa janan, joka kulkee pisteiden (l1x, l1y) & (l2x, l2y) kaitta
	;  False (0) muulloin
	
	;Ympyrän keskipisteen ja (ainakin toisen) janan päätepisteen etäisyys < r
	;-> leikkaus
	If Distance(cx, llx, cy, l1y) <= r Then
		Return True
	EndIf
	
	If Distance(cx, l2x, cy, l2y) <= r Then
		Return True
	EndIf	
	
	;Vektorit (janan vektori ja vektorit janan päätepisteistä ympyrän keskipisteeseen)
	Local SegVecX# = l2x - l1x
	Local SegVecY# = l2y - l1y
	
	Local PntVec1X# = cx - l1x
	Local PntVec1Y# = cy - l1y
	
	Local PntVec2X# = cx - l2x
	Local PntVec2Y# = cy - l2y
	
	;Em. vektorien pistetulot
	Local dp1# = SegVecX * PntVec1X + SegVecY * PntVec1Y
	Local dp2# = -SegVecX * PntVec2X - SegVecY * PntVec2Y
	
	;Tarkistaa onko toisen pistetulon arvo 0
	;tai molempien merkki sama
	If dp1 = 0 Lor dp2 = 0 Then
	ElseIf (dp1 > 0 And dp2 > 0) Lor (dp1 < 0 And dp2 < 0) Then
	Else
		;Ei kumpikaan -> ei leikkausta
		Return False
	EndIf
	
	;Janan päätepisteiden kautta kulkevan suoran ;yhtälö; (ax + by + c = 0)
	Local a# = (l2y - l1y) / (l2x - l1x)
	Local b# = -1
	Local c# = -(l2y - l1y) / (l2x - l1x) * l1x + l1y
	
	;Ympyrän keskipisteen etäisyys suorasta
	Local d# = Abs(a * cx + b * cy + c) / Sqr(a * a + b * b)
	
	;Ympyrä on liian kaukana
	;-> ei leikkausta
	If d > r Then Return False
	
	;Local kateetin_pituus# = Cos(angle) * hyp
	
	;Jos päästään tänne saakka, ympyrä ja jana leikkaavat (tai ovat sisäkkäin)
	Return True
End Function

;Weight < 1: Bias towards Max
;Weight > 1: Bias towards Min
Function RandWithBias(Min%, Max%, Weight#)
	Return Floor(Min + (Max + 1 - Min) * (Rnd(1)^Weight))
End Function

Function point_direction#(x1#,z1#,x2#,z2#)
	Local dx#, dz#
	dx = x1 - x2
	dz = z1 - z2
	Return ATan2(dz,dx)
End Function

Function angleDist#(a0#,a1#)
	Local b# = a0-a1
	Local bb#
	If b<-180.0 Then
		bb = b+360.0
	Else If b>180.0 Then
		bb = b-360.0
	Else
		bb = b
	EndIf
	Return bb
End Function

Function Inverse#(number#)
	
	Return Float(1.0-number#)
	
End Function

Function Rnd_Array#(numb1#,numb2#,Array1#,Array2#)
	Local whatarray% = Rand(1,2)
	
	If whatarray% = 1
		Return Rnd(Array1#,numb1#)
	Else
		Return Rnd(numb2#,Array2#)
	EndIf
	
End Function