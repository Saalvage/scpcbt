;achievement menu & messages by InnocentSam

Global Achievements%[MAXACHIEVEMENTS]
Global AchievementStrings$[MAXACHIEVEMENTS]
Global AchievementDescs$[MAXACHIEVEMENTS]
Global AchvIMG%[MAXACHIEVEMENTS]

Global UsedConsole

Global AchievementsMenu%
Global AchvMSGenabled% = GetINIInt(OptionFile, "options", "achievement popup enabled")
For i = 0 To MAXACHIEVEMENTS
	Local loc2 = GetINISectionLocation(AchvIni, "s"+Str(i))
	AchievementStrings[i] = GetINIString2(AchvIni, loc2, "name")
	AchievementDescs[i] = GetINIString2(AchvIni, loc2, "desc")
	
	Local image$ = GetINIString2(AchvIni, loc2, "image")
	
	AchvIMG[i] = LoadImage_Strict("GFX\menu\achievements\"+image+".jpg")
	AchvIMG[i] = ResizeImage2(AchvIMG[i],ImageWidth(AchvIMG[i])*I_Opt\GraphicHeight/768.0,ImageHeight(AchvIMG[i])*I_Opt\GraphicHeight/768.0)
Next

Global AchvLocked = LoadImage_Strict("GFX\menu\achievements\achvlocked.jpg")
AchvLocked = ResizeImage2(AchvLocked,ImageWidth(AchvLocked)*I_Opt\GraphicHeight/768.0,ImageHeight(AchvLocked)*I_Opt\GraphicHeight/768.0)

Function GiveAchievement(achvname%, showMessage%=True)
	If Achievements[achvname]<>True Then
		Achievements[achvname]=True
		If AchvMSGenabled And showMessage Then
			Local loc2% = GetINISectionLocation(AchvIni, "s"+achvname)
			Local AchievementName$ = GetINIString2(AchvIni, loc2, "name")
			CreateAchievementMsg(achvname,AchievementName)
		EndIf
	EndIf
End Function

Function AchievementTooltip(achvno%)

	Local scale# = I_Opt\GraphicHeight/768.0
	
	SetFont I_Opt\Fonts[3]
	Local width = StringWidth(AchievementStrings[achvno])
	SetFont I_Opt\Fonts[1]
	If (StringWidth(AchievementDescs[achvno])>width) Then
		width = StringWidth(AchievementDescs[achvno])
	EndIf
	width = width+20*MenuScale
	
	Local height = 38*scale
	
	Color 25,25,25
	Rect(ScaledMouseX()+(20*MenuScale),ScaledMouseY()+(20*MenuScale),width,height,True)
	Color 150,150,150
	Rect(ScaledMouseX()+(20*MenuScale),ScaledMouseY()+(20*MenuScale),width,height,False)
	SetFont I_Opt\Fonts[3]
	Text(ScaledMouseX()+(20*MenuScale)+(width/2),ScaledMouseY()+(35*MenuScale), AchievementStrings[achvno], True, True)
	SetFont I_Opt\Fonts[1]
	Text(ScaledMouseX()+(20*MenuScale)+(width/2),ScaledMouseY()+(55*MenuScale), AchievementDescs[achvno], True, True)
End Function

Function DrawAchvIMG(x%, y%, achvno%)
	
	Local row%
	Local scale# = I_Opt\GraphicHeight/768.0
	Local SeparationConst2 = 76 * scale
	row = achvno Mod 4
	Color 0,0,0
	Rect((x+((row)*SeparationConst2)), y, 64*scale, 64*scale, True)
	If Achievements[achvno] = True Then
		DrawImage(AchvIMG[achvno],(x+(row*SeparationConst2)),y)
	Else
		DrawImage(AchvLocked,(x+(row*SeparationConst2)),y)
	EndIf
	Color 50,50,50
	
	Rect((x+(row*SeparationConst2)), y, 64*scale, 64*scale, False)
End Function

Global CurrAchvMSGID% = 0

Type AchievementMsg
	Field achvID%
	Field txt$
	Field msgx#
	Field msgtime#
	Field msgID%
End Type

Function CreateAchievementMsg.AchievementMsg(id%,txt$)
	Local amsg.AchievementMsg = New AchievementMsg
	
	amsg\achvID = id
	amsg\txt = txt
	amsg\msgx = 0.0
	amsg\msgtime = FPSfactor2
	amsg\msgID = CurrAchvMSGID
	CurrAchvMSGID = CurrAchvMSGID + 1
	
	Return amsg
End Function

Function UpdateAchievementMsg()

	Local amsg.AchievementMsg,amsg2.AchievementMsg
	Local scale# = I_Opt\GraphicHeight/768.0
	Local width% = 264*scale
	Local height% = 84*scale
	Local x%,y%
	
	For amsg = Each AchievementMsg
		If amsg\msgtime <> 0
			x=I_Opt\GraphicWidth+amsg\msgx
			y=(I_Opt\GraphicHeight-height)
			For amsg2 = Each AchievementMsg
				If amsg2 <> amsg
					If amsg2\msgID > amsg\msgID
						y=y-height
					EndIf
				EndIf
			Next
			DrawFrame(x,y,width,height)
			Color 0,0,0
			Rect(x+10*scale,y+10*scale,64*scale,64*scale,True)
			DrawImage(AchvIMG[amsg\achvID],x+10*scale,y+10*scale)
			Color 50,50,50
			Rect(x+10*scale,y+10*scale,64*scale,64*scale,False)
			Color 255,255,255
			SetFont I_Opt\Fonts[1]
			RowText(GetLocalString("Messages", "achvunlock")+" - "+amsg\txt,x+84*scale,y+10*scale,width-94*scale,y-20*scale)
			If amsg\msgtime > 0.0 And amsg\msgtime < 70*7
				amsg\msgtime = amsg\msgtime + FPSfactor2
				If amsg\msgx > -width%
					amsg\msgx = Max(amsg\msgx-4*FPSfactor2,-width%)
				EndIf
			ElseIf amsg\msgtime >= 70*7
				amsg\msgtime = -1
			ElseIf amsg\msgtime = -1
				If amsg\msgx < 0.0
					amsg\msgx = Min(amsg\msgx+4*FPSfactor2,0.0)
				Else
					amsg\msgtime = 0.0
				EndIf
			EndIf
		Else
			Delete amsg
		EndIf
	Next
	
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D