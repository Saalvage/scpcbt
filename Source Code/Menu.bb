Global MenuBack% = LoadImage_Strict("GFX\menu\back.jpg")
Global MenuText% = LoadImage_Strict("GFX\menu\scptext.jpg")
Global Menu173% = LoadImage_Strict("GFX\menu\173back.jpg")
MenuWhite = LoadImage_Strict("GFX\menu\menuwhite.jpg")
MenuBlack = LoadImage_Strict("GFX\menu\menublack.jpg")
MaskImage MenuBlack, 255,255,0

ResizeImage(MenuBack, ImageWidth(MenuBack) * MenuScale, ImageHeight(MenuBack) * MenuScale)
ResizeImage(MenuText, ImageWidth(MenuText) * MenuScale, ImageHeight(MenuText) * MenuScale)
ResizeImage(Menu173, ImageWidth(Menu173) * MenuScale, ImageHeight(Menu173) * MenuScale)

For i = 0 To 3
	ArrowIMG[i] = LoadImage_Strict("GFX\menu\arrow.png")
	RotateImage(ArrowIMG[i], 90 * i)
	HandleImage(ArrowIMG[i], 0, 0)
Next

Global RandomSeed$

Dim MenuBlinkTimer%(2), MenuBlinkDuration%(2)
MenuBlinkTimer%(0) = 1
MenuBlinkTimer%(1) = 1

Global MenuStr$, MenuStrX%, MenuStrY%

Global MainMenuTab%


Global IntroEnabled% = GetINIInt(OptionFile, "options", "intro enabled")

Global SelectedInputBox%, CursorPos% = -1

Global RandSeedAmount% = 1
While GetLocalString("Menu", "randseed" + RandSeedAmount) <> ""
	RandSeedAmount = RandSeedAmount + 1
Wend

Global CurrLoadGamePage% = 0

Function UpdateMainMenu()
	
	Local x%, y%, width%, height%, temp%
	
	Color 0,0,0
	Rect 0,0,I_Opt\GraphicWidth,I_Opt\GraphicHeight,True
	
	ShowPointer()
	
	DrawImage(MenuBack, 0, 0)
	
	If (MilliSecs() Mod MenuBlinkTimer(0)) >= Rand(MenuBlinkDuration(0)) Then
		DrawImage(Menu173, I_Opt\GraphicWidth - ImageWidth(Menu173), I_Opt\GraphicHeight - ImageHeight(Menu173))
	EndIf
	
	If Rand(300) = 1 Then
		MenuBlinkTimer(0) = Rand(4000, 8000)
		MenuBlinkDuration(0) = Rand(200, 500)
	EndIf
	
	SetFont I_Opt\Fonts[1]
	
	MenuBlinkTimer(1)=MenuBlinkTimer(1)-FPSfactor
	If MenuBlinkTimer(1) < MenuBlinkDuration(1) Then
		Color(50, 50, 50)
		Text(MenuStrX + Rand(-5, 5), MenuStrY + Rand(-5, 5), MenuStr, True)
		If MenuBlinkTimer(1) < 0 Then
			MenuBlinkTimer(1) = Rand(700, 800)
			MenuBlinkDuration(1) = Rand(10, 35)
			MenuStrX = Rand(700, 1000) * MenuScale
			MenuStrY = Rand(100, 600) * MenuScale
			
			Select Rand(0, 22)
				Case 0, 2, 3
					MenuStr = GetLocalString("Menu", "blink")
				Case 4, 5
					MenuStr = GetLocalString("Menu", "scp")
				Case 6, 7, 8
					MenuStr = GetLocalString("Menu", "happyendings")
				Case 9, 10, 11
					MenuStr = GetLocalString("Menu", "scream")
				Case 12, 19
					MenuStr = GetLocalString("Menu", "nil")
				Case 13
					MenuStr = GetLocalString("Menu", "no")
				Case 14
					MenuStr = GetLocalString("Menu", "gray")
				Case 15
					MenuStr = GetLocalString("Menu", "stone")
				Case 16
					MenuStr = GetLocalString("Menu", "9341")
				Case 17
					MenuStr = GetLocalString("Menu", "doors")
				Case 18
					MenuStr = GetLocalString("Menu", "e")
				Case 20
					MenuStr = GetLocalString("Menu", "everything")
				Case 21
					MenuStr = GetLocalString("Menu", "spiral")
				Case 22
					MenuStr = GetLocalString("Menu", "gestalt")
			End Select
		EndIf
	EndIf
	
	SetFont I_Opt\Fonts[2]
	
	DrawImage(MenuText, I_Opt\GraphicWidth / 2 - ImageWidth(MenuText) / 2, I_Opt\GraphicHeight - 20 * MenuScale - ImageHeight(MenuText))
	
	If I_Opt\GraphicWidth > 1240 * MenuScale Then
		DrawTiledImageRect(MenuWhite, 0, 5, 512, 7 * MenuScale, 985.0 * MenuScale, 407.0 * MenuScale, (I_Opt\GraphicWidth - 1240 * MenuScale) + 300, 7 * MenuScale)
	EndIf
	
	If (Not MouseDown1)
		OnSliderID = 0
	EndIf
	
	If MainMenuTab = 0 Then
		For i% = 0 To 3
			temp = False
			x = 159 * MenuScale
			y = (286 + 100 * i) * MenuScale
			
			width = 400 * MenuScale
			height = 70 * MenuScale
			
			temp = (MouseHit1 And MouseOn(x, y, width, height))
			
			Local txt$
			Select i
				Case 0
					txt = GetLocalString("Menu", "newgame")
					If temp Then
						If I_Opt\DebugMode Then
							RandomSeed = "666"
						Else
							If Rand(15)=1 Then 
								RandomSeed = GetLocalString("Menu", "randseed" + Rand(RandSeedAmount)) ;I know that this can produce an invalid randseed, but "" is worthy of being a randseed as well! ~Salvage
							Else
								RandomSeed = ""
								n = Rand(4,8)
								For i = 1 To n
									If Rand(3)=1 Then
										RandomSeed = RandomSeed + Rand(0,9)
									Else
										RandomSeed = RandomSeed + Chr(Rand(97,122))
									EndIf
								Next							
							EndIf
						EndIf

						SelectedDifficulty = difficulties[EUCLID]
						
						LoadSaveGames()
						CurrSave = New Save
						
						MainMenuTab = 1
					EndIf
				Case 1
					txt = Upper(GetLocalString("Menu", "loadgame"))
					If temp Then
						LoadSaveGames()
						MainMenuTab = 2
					EndIf
				Case 2
					txt = Upper(GetLocalString("Menu", "options"))
					If temp Then MainMenuTab = 3
				Case 3
					txt = Upper(GetLocalString("Menu", "quit"))
					If temp Then
						StopChannel(CurrMusicStream)
						End
					EndIf
			End Select
			
			DrawButton(x, y, width, height, txt)
		Next	
		
	Else
		
		x = 159 * MenuScale
		y = 286 * MenuScale
		
		width = 400 * MenuScale
		height = 70 * MenuScale
		
		DrawFrame(x, y, width, height)
		
		If DrawButton(x + width + 20 * MenuScale, y, 580 * MenuScale - width - 20 * MenuScale, height, Upper(GetLocalString("Menu", "back")), False) Then 
			Select MainMenuTab
				Case 1
					PutINIValue(OptionFile, "options", "intro enabled", IntroEnabled%)
					For I_SAV.Save = Each Save
						Delete I_SAV
					Next
					MainMenuTab = 0
				Case 2
					CurrLoadGamePage = 0
					For I_SAV.Save = Each Save
						Delete I_SAV
					Next
					MainMenuTab = 0
				Case 3,5,6,7 ;save the options
					SaveOptionsINI()
					
					UserTrackCheck% = 0
					UserTrackCheck2% = 0
					
					AntiAlias Opt_AntiAlias
					MainMenuTab = 0
				Default
					MainMenuTab = 0
			End Select
		EndIf
		
		Select MainMenuTab
			Case 1 ; New game
				
				x = 159 * MenuScale
				y = 286 * MenuScale
				
				width = 400 * MenuScale
				height = 70 * MenuScale
				
				Color(255, 255, 255)
				SetFont I_Opt\Fonts[2]
				Text(x + width / 2, y + height / 2, GetLocalString("Menu", "newgame"), True, True)
				
				x = 160 * MenuScale
				y = y + height + 20 * MenuScale
				width = 580 * MenuScale
				height = 335 * MenuScale
				
				DrawFrame(x, y, width, height)				
				
				SetFont I_Opt\Fonts[1]
				
				Text (x + 20 * MenuScale, y + 20 * MenuScale, GetLocalString("Menu", "name")+":")
				CurrSave\Name = InputBox(x + 150 * MenuScale, y + 15 * MenuScale, 200 * MenuScale, 30 * MenuScale, CurrSave\Name, 1, 15)
				If SelectedInputBox = 1 Then
					CurrSave\Name = Replace(CurrSave\Name,":","")
					CurrSave\Name = Replace(CurrSave\Name,".","")
					CurrSave\Name = Replace(CurrSave\Name,"/","")
					CurrSave\Name = Replace(CurrSave\Name,"\","")
					CurrSave\Name = Replace(CurrSave\Name,"<","")
					CurrSave\Name = Replace(CurrSave\Name,">","")
					CurrSave\Name = Replace(CurrSave\Name,"|","")
					CurrSave\Name = Replace(CurrSave\Name,"?","")
					CurrSave\Name = Replace(CurrSave\Name,Chr(34),"")
					CurrSave\Name = Replace(CurrSave\Name,"*","")
					CursorPos = Min(CursorPos, Len(CurrSave\Name))
				EndIf
				
				Color 255,255,255
				Text (x + 20 * MenuScale, y + 60 * MenuScale, GetLocalString("Menu", "seed")+":")
				RandomSeed = InputBox(x+150*MenuScale, y+55*MenuScale, 200*MenuScale, 30*MenuScale, RandomSeed, 3, 15)
				
				Text(x + 20 * MenuScale, y + 110 * MenuScale, GetLocalString("Menu", "intro")+":")
				IntroEnabled = DrawTick(x + 280 * MenuScale, y + 110 * MenuScale, IntroEnabled)	
				
				;Local modeName$, modeDescription$, selectedDescription$
				Text (x + 20 * MenuScale, y + 150 * MenuScale, GetLocalString("Menu", "difficulty"))				
				For i = SAFE To CUSTOM
					If DrawTick(x + 20 * MenuScale, y + (180+30*i) * MenuScale, (SelectedDifficulty = difficulties[i]), (i = APOLLYON) And (Not ApolUnlocked)) Then SelectedDifficulty = difficulties[i]
					Color(difficulties[i]\r,difficulties[i]\g,difficulties[i]\b)
					Text(x + 60 * MenuScale, y + (180+30*i) * MenuScale, difficulties[i]\name)
				Next
				
				Color(255, 255, 255)
				DrawFrame(x + 150 * MenuScale,y + 155 * MenuScale, 410*MenuScale, 160*MenuScale)
				
				If SelectedDifficulty\customizable Then
					SelectedDifficulty\permaDeath =  DrawTick(x + 160 * MenuScale, y + 165 * MenuScale, (SelectedDifficulty\permaDeath))
					Text(x + 200 * MenuScale, y + 165 * MenuScale, GetLocalString("Menu", "permadeath"))
					
					If DrawTick(x + 160 * MenuScale, y + 195 * MenuScale, SelectedDifficulty\saveType = SAVEANYWHERE And (Not SelectedDifficulty\permaDeath)) Then
						SelectedDifficulty\permaDeath = 0
						SelectedDifficulty\saveType = SAVEANYWHERE
					Else
						SelectedDifficulty\saveType = SAVEONSCREENS
					EndIf
					
					Text(x + 200 * MenuScale, y + 195 * MenuScale, GetLocalString("Menu", "savea"))	
					
					SelectedDifficulty\aggressiveNPCs =  DrawTick(x + 160 * MenuScale, y + 225 * MenuScale, SelectedDifficulty\aggressiveNPCs)
					Text(x + 200 * MenuScale, y + 225 * MenuScale, GetLocalString("Menu", "anpcs"))
					
					;Items
					Color 255,255,255
					DrawImage ArrowIMG[3],x + 155 * MenuScale, y+251*MenuScale
					DrawImage ArrowIMG[1],x + 405 * MenuScale, y+251*MenuScale
					
					If MouseHit1
						If ImageRectOverlap(ArrowIMG[3],x + 405 * MenuScale, y+251*MenuScale, ScaledMouseX(),ScaledMouseY(),0,0)
							SelectedDifficulty\items = SelectedDifficulty\items + 2
							If SelectedDifficulty\items = 3 Then
								SelectedDifficulty\items = 2
							ElseIf SelectedDifficulty\items > 20 Then
								SelectedDifficulty\items = 1
							EndIf
							PlaySound_Strict(ButtonSFX)
						ElseIf ImageRectOverlap(ArrowIMG[1],x + 155 * MenuScale, y+251*MenuScale, ScaledMouseX(),ScaledMouseY(),0,0)
							SelectedDifficulty\items = SelectedDifficulty\items - 2
							If SelectedDifficulty\items = 0 Then
								SelectedDifficulty\items = 1
							ElseIf SelectedDifficulty\items < 0 Then
								SelectedDifficulty\items = 20
							EndIf
							PlaySound_Strict(ButtonSFX)
						EndIf
					EndIf
					
					Text(x + 200 * MenuScale, y + 255 * MenuScale, GetLocalString("Menu", "items") + ": " + SelectedDifficulty\items)
					
					;Other factor's difficulty
					DrawImage ArrowIMG[1],x + 155 * MenuScale, y+281*MenuScale
					If MouseHit1
						If ImageRectOverlap(ArrowIMG[1],x + 155 * MenuScale, y+281*MenuScale, ScaledMouseX(),ScaledMouseY(),0,0)
							SelectedDifficulty\otherFactors = (SelectedDifficulty\otherFactors + 1) Mod (3 + ApolUnlocked)
							PlaySound_Strict(ButtonSFX)
						EndIf
					EndIf
					Color 255,255,255
					Text(x + 200 * MenuScale, y + 285 * MenuScale, GetLocalString("Menu", "odf" + SelectedDifficulty\otherFactors))
				Else
					RowText(SelectedDifficulty\description, x+160*MenuScale, y+160*MenuScale, (410-20)*MenuScale, 200)					
				EndIf
				
				SetFont I_Opt\Fonts[2]
				
				If DrawButton(x + 420 * MenuScale, y + height + 20 * MenuScale, 160 * MenuScale, 70 * MenuScale, GetLocalString("Menu", "start"), False) Then
					
					If CurrSave\Name = "" Then CurrSave\Name = GetLocalString("Menu", "untitled")
					
					If RandomSeed = "" Then
						RandomSeed = Abs(MilliSecs())
					EndIf
					
					SeedRnd GenerateSeedNumber(RandomSeed)
					
					Local SameFound% = 0
					Local LowestPossible% = 2
					
					For  I_SAV.Save = Each Save
						If (CurrSave <> I_SAV And CurrSave\Name = I_SAV\Name) Then SameFound = 1 : Exit
					Next
						
					While SameFound = 1
						SameFound = 2
						For I_SAV.Save = Each Save
							If (I_SAV\Name = (CurrSave\Name + " (" + LowestPossible + ")")) Then LowestPossible = LowestPossible + 1 : SameFound = True : Exit
						Next
					Wend
					
					If SameFound = 2 Then CurrSave\Name = CurrSave\Name + " (" + LowestPossible + ")"
					
					InitNewGame(Clamp(Int(Left(CurrSave\Name, 1)), 0, 2)) ;ONLY FOR DEBUGGING
					MainMenuOpen = False
					FlushKeys()
					FlushMouse()
					
					PutINIValue(OptionFile, "options", "intro enabled", IntroEnabled%)
					
				EndIf
				

			Case 2 ;load game
				y = y + height + 20 * MenuScale
				width = 580 * MenuScale
				height = 430 * MenuScale
				
				DrawFrame(x, y, width, height)
				
				x = 159 * MenuScale
				y = 286 * MenuScale
				
				width = 400 * MenuScale
				height = 70 * MenuScale
				
				Color(255, 255, 255)
				SetFont I_Opt\Fonts[2]
				Text(x + width / 2, y + height / 2, Upper(GetLocalString("Menu", "loadgame")), True, True)
				
				x = 160 * MenuScale
				y = y + height + 20 * MenuScale
				width = 580 * MenuScale
				height = 296 * MenuScale	
				
				SetFont I_Opt\Fonts[2]
				
				If CurrLoadGamePage < Ceil(Float(SaveGameAmount)/5.0)-1 And DelSave = Null Then 
					If DrawButton(x+530*MenuScale, y + 440*MenuScale, 50*MenuScale, 55*MenuScale, ">") Then
						CurrLoadGamePage = CurrLoadGamePage+1
					EndIf
				Else
					DrawFrame(x+530*MenuScale, y + 440*MenuScale, 50*MenuScale, 55*MenuScale)
					Color(100, 100, 100)
					Text(x+555*MenuScale, y + 467.5*MenuScale, ">", True, True)
				EndIf
				If CurrLoadGamePage > 0 And DelSave = Null Then
					If DrawButton(x, y + 440*MenuScale, 50*MenuScale, 55*MenuScale, "<") Then
						CurrLoadGamePage = CurrLoadGamePage-1
					EndIf
				Else
					DrawFrame(x, y + 440*MenuScale, 50*MenuScale, 55*MenuScale)
					Color(100, 100, 100)
					Text(x+25*MenuScale, y + 467.5*MenuScale, "<", True, True)
				EndIf
				
				DrawFrame(x+60*MenuScale,y+440*MenuScale,width-120*MenuScale,55*MenuScale)
				
				Text(x+(width/2.0),y+466*MenuScale,GetLocalString("Menu", "page")+" "+Int(Max((CurrLoadGamePage+1),1))+"/"+Int(Max((Int(Ceil(Float(SaveGameAmount)/5.0))),1)),True,True)
				
				SetFont I_Opt\Fonts[1]
				
				If CurrLoadGamePage > Ceil(Float(SaveGameAmount)/5.0)-1 Then
					CurrLoadGamePage = CurrLoadGamePage - 1
				EndIf
				
				If ((First Save) = Null) Then
					Text(x + 20 * MenuScale, y + 20 * MenuScale, GetLocalString("Menu", "nosavegames"))
				Else
					x = x + 20 * MenuScale
					y = y + 20 * MenuScale
					
					CurrSave = First Save
					
					For i% = 0 To 4+(5*CurrLoadGamePage)
						If i > 0 Then CurrSave = After CurrSave
						If i >= (0+(5*CurrLoadGamePage))
							DrawFrame(x,y,540* MenuScale, 70* MenuScale)
							
							If CurrSave\Version <> CompatibleNumber Then
								Color 255,0,0
							Else
								Color 255,255,255
							EndIf
							
							Text(x + 20 * MenuScale, y + 10 * MenuScale, CurrSave\Name)
							Text(x + 20 * MenuScale, y + (10+18) * MenuScale, CurrSave\Time) ;y + (10+23) * MenuScale
							Text(x + 120 * MenuScale, y + (10+18) * MenuScale, CurrSave\Date)
							Text(x + 20 * MenuScale, y + (10+36) * MenuScale, CurrSave\Version)
							Text(x + 120 * MenuScale, y + (10+36) * MenuScale, CurrSave\PlayTime)
							
							If DelSave = Null Then
								If DrawButton(x + 400 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale, GetLocalString("Menu", "delete"), False) Then
									DelSave = CurrSave
									Exit
								EndIf
								If CurrSave\Version <> CompatibleNumber Then
									DrawFrame(x + 280 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale)
									Color(255, 0, 0)
									Text(x + 330 * MenuScale, y + 34 * MenuScale, GetLocalString("Menu", "load"), True, True)
								Else
									If DrawButton(x + 280 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale, GetLocalString("Menu", "load"), False) Then
										LoadEntities()
										LoadAllSounds()
										LoadGame(CurrSave\Name)
										InitLoadGame()
										MainMenuOpen = False
										Exit
									EndIf
								EndIf
							Else
								DrawFrame(x + 280 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale)
								If CurrSave\Version <> CompatibleNumber Then
									Color(255, 0, 0)
								Else
									Color(100, 100, 100)
								EndIf
								Text(x + 330 * MenuScale, y + 34 * MenuScale, GetLocalString("Menu", "load"), True, True)
								
								DrawFrame(x + 400 * MenuScale, y + 20 * MenuScale, 100 * MenuScale, 30 * MenuScale)
								Color(100, 100, 100)
								Text(x + 450 * MenuScale, y + 34 * MenuScale, GetLocalString("Menu", "delete"), True, True)
							EndIf
							
							If CurrSave = Last Save Then
								Exit
							EndIf
							
							y = y + 80 * MenuScale
							
						EndIf
					Next
					
					If DelSave <> Null
						x = 740 * MenuScale
						y = 376 * MenuScale
						DrawFrame(x, y, 300 * MenuScale, 150 * MenuScale)
						RowText(GetLocalString("Menu", "deletesave"), x + 20 * MenuScale, y + 15 * MenuScale, 275 * MenuScale, 200 * MenuScale)
						If DrawButton(x + 25 * MenuScale, y + 100 * MenuScale, 100 * MenuScale, 30 * MenuScale, GetLocalString("Menu", "yes"), False) Then
							DeleteGame(DelSave)
						EndIf
						If DrawButton(x + 175 * MenuScale, y + 100 * MenuScale, 100 * MenuScale, 30 * MenuScale, GetLocalString("Menu", "no"), False) Then
							DelSave = Null
						EndIf
					EndIf
				EndIf
				
			Case 3,5,6,7 ;options
				
				x = 159 * MenuScale
				y = 286 * MenuScale
				
				width = 400 * MenuScale
				height = 70 * MenuScale
				
				Color(255, 255, 255)
				SetFont I_Opt\Fonts[2]
				Text(x + width / 2, y + height / 2, Upper(GetLocalString("Menu", "options")), True, True)
				
				x = 160 * MenuScale
				y = y + height + 20 * MenuScale
				width = 580 * MenuScale
				height = 60 * MenuScale
				DrawFrame(x, y, width, height)
				
				Color 0,255,0
				Select MainMenuTab
					Case 3
						Rect(x+15*MenuScale,y+10*MenuScale,(width/5)+10*MenuScale,(height/2)+10*MenuScale,True)
					Case 5
						Rect(x+155*MenuScale,y+10*MenuScale,(width/5)+10*MenuScale,(height/2)+10*MenuScale,True)
					Case 6
						Rect(x+295*MenuScale,y+10*MenuScale,(width/5)+10*MenuScale,(height/2)+10*MenuScale,True)
					Case 7
						Rect(x+435*MenuScale,y+10*MenuScale,(width/5)+10*MenuScale,(height/2)+10*MenuScale,True)
				End Select
				
				Color 255,255,255
				If DrawButton(x+20*MenuScale,y+15*MenuScale,width/5,height/2, GetLocalString("Options", "graphics"), False) Then MainMenuTab = 3
				If DrawButton(x+160*MenuScale,y+15*MenuScale,width/5,height/2, GetLocalString("Options", "audio"), False) Then MainMenuTab = 5
				If DrawButton(x+300*MenuScale,y+15*MenuScale,width/5,height/2, GetLocalString("Options", "control"), False) Then MainMenuTab = 6
				If DrawButton(x+440*MenuScale,y+15*MenuScale,width/5,height/2, GetLocalString("Options", "advanced"), False) Then MainMenuTab = 7
				
				SetFont I_Opt\Fonts[1]
				y = y + 70 * MenuScale
				
				If MainMenuTab <> 5
					UserTrackCheck% = 0
					UserTrackCheck2% = 0
				EndIf
				
				Local tx# = x+width
				Local ty# = y
				Local tw# = 400*MenuScale
				Local th# = 150*MenuScale
				
				;DrawOptionsTooltip(tx,ty,tw,th,"")
				
				Select MainMenuTab
					Case 3 ;Graphics
						height = 380 * MenuScale
						DrawFrame(x, y, width, height)
						
						y=y+20*MenuScale
						
						Color 255,255,255				
						Text(x + 20 * MenuScale, y, GetLocalString("Options", "bumpmap"))					
						BumpEnabled = DrawTick(x + 310 * MenuScale, y + MenuScale, BumpEnabled)
						If MouseOn(x + 310 * MenuScale, y + MenuScale, 20*MenuScale,20*MenuScale) And OnSliderID=0
							;DrawTooltip("Not available in this version")
							DrawOptionsTooltip(tx,ty,tw,th,"bump")
						EndIf
						
						y=y+30*MenuScale
						
						Color 255,255,255
						Text(x + 20 * MenuScale, y, GetLocalString("Options", "vsync"))
						Vsync% = DrawTick(x + 310 * MenuScale, y + MenuScale, Vsync%)
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale) And OnSliderID=0
							DrawOptionsTooltip(tx,ty,tw,th,"vsync")
						EndIf
						
						y=y+30*MenuScale
						
						Color 255,255,255
						Text(x + 20 * MenuScale, y, GetLocalString("Options", "antialias"))
						Opt_AntiAlias = DrawTick(x + 310 * MenuScale, y + MenuScale, Opt_AntiAlias%)
						;Text(x + 20 * MenuScale, y + 15 * MenuScale, "(fullscreen mode only)")
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale) And OnSliderID=0
							DrawOptionsTooltip(tx,ty,tw,th,"antialias")
						EndIf
						
						y=y+30*MenuScale ;40
						
						Color 255,255,255
						Text(x + 20 * MenuScale, y, GetLocalString("Options", "roomlights"))
						EnableRoomLights = DrawTick(x + 310 * MenuScale, y + MenuScale, EnableRoomLights)
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale) And OnSliderID=0
							DrawOptionsTooltip(tx,ty,tw,th,"roomlights")
						EndIf
						
						y=y+30*MenuScale
						
						Color 255,255,255
						Text(x + 20 * MenuScale, y, GetLocalString("Options", "vram"))
						EnableVRam = DrawTick(x + 310 * MenuScale, y + MenuScale, EnableVRam)
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale) And OnSliderID=0
							DrawOptionsTooltip(tx,ty,tw,th,"vram")
						EndIf
						
						y=y+30*MenuScale
						
						;Local prevGamma# = ScreenGamma
						ScreenGamma = (SlideBar(x + 310*MenuScale, y+6*MenuScale, 150*MenuScale, ScreenGamma*50.0)/50.0)
						Color 255,255,255
						Text(x + 20 * MenuScale, y, GetLocalString("Options", "gamma"))
						If MouseOn(x+310*MenuScale,y+6*MenuScale,150*MenuScale+14,20) And OnSliderID=0
							DrawOptionsTooltip(tx,ty,tw,th,"gamma",ScreenGamma)
						EndIf
						
						y=y+50*MenuScale
						
						Color 255,255,255
						Text(x + 20 * MenuScale, y, GetLocalString("Options", "pamount"))
						ParticleAmount = Slider3(x+310*MenuScale,y+6*MenuScale,150*MenuScale,ParticleAmount,2, GetLocalString("Options", "minimal"),GetLocalString("Options", "reduced"),GetLocalString("Options", "full"))
						If (MouseOn(x + 310 * MenuScale, y-6*MenuScale, 150*MenuScale+14, 20) And OnSliderID=0) Lor OnSliderID=2
							DrawOptionsTooltip(tx,ty,tw,th,"particleamount",ParticleAmount)
						EndIf
						
						y=y+50*MenuScale
						
						Color 255,255,255
						Text(x + 20 * MenuScale, y, GetLocalString("Options", "lod"))
						TextureDetails = Slider5(x+310*MenuScale,y+6*MenuScale,150*MenuScale,TextureDetails,3,"0.8","0.4","0.0","-0.4","-0.8")
						Select TextureDetails%
							Case 0
								TextureFloat# = 0.8
							Case 1
								TextureFloat# = 0.4
							Case 2
								TextureFloat# = 0.0
							Case 3
								TextureFloat# = -0.4
							Case 4
								TextureFloat# = -0.8
						End Select
						TextureLodBias TextureFloat
						If (MouseOn(x+310*MenuScale,y-6*MenuScale,150*MenuScale+14,20) And OnSliderID=0) Lor OnSliderID=3
							DrawOptionsTooltip(tx,ty,tw,th+100*MenuScale,"texquality")
						EndIf
						
						y=y+50*MenuScale
						
						Local SlideBarFOV% = FOV-40
						SlideBarFOV = (SlideBar(x + 310*MenuScale, y+6*MenuScale,150*MenuScale, SlideBarFOV*2.0)/2.0)
						FOV = SlideBarFOV+40
						Color 255,255,255
						Text(x + 20 * MenuScale, y, GetLocalString("Options", "fov"))
						Color 255,255,0
						Text(x + 25 * MenuScale, y + 25 * MenuScale, FOV +" FOV")
						If MouseOn(x+310*MenuScale,y+6*MenuScale,150*MenuScale+14,20)
							DrawOptionsTooltip(tx,ty,tw,th,"fov")
						EndIf
					Case 5 ;Audio
						Local PrevEnableUserTracks = EnableUserTracks
						
						If PrevEnableUserTracks
							height = 210 * MenuScale
						Else
							height = 145 * MenuScale
						EndIf
						
						DrawFrame(x, y, width, height)	
						
						y = y + 20*MenuScale
						
						MusicVolume = (SlideBar(x + 310*MenuScale, y-4*MenuScale, 150*MenuScale, MusicVolume*100.0)/100.0)
						Color 255,255,255
						Text(x + 20 * MenuScale, y, GetLocalString("Options", "musicv"))
						If MouseOn(x+310*MenuScale,y-4*MenuScale,150*MenuScale+14,20)
							DrawOptionsTooltip(tx,ty,tw,th,"musicvol",MusicVolume)
						EndIf
						
						y = y + 30*MenuScale
						
						PrevSFXVolume = (SlideBar(x + 310*MenuScale, y-4*MenuScale, 150*MenuScale, SFXVolume*100.0)/100.0)
						SFXVolume = PrevSFXVolume
						Color 255,255,255
						Text(x + 20 * MenuScale, y, GetLocalString("Options", "soundv"))
						If MouseOn(x+310*MenuScale,y-4*MenuScale,150*MenuScale+14,20)
							DrawOptionsTooltip(tx,ty,tw,th,"soundvol",PrevSFXVolume)
						EndIf
						
						y = y + 30*MenuScale
						
						Color 255,255,255
						Text x + 20 * MenuScale, y, GetLocalString("Options", "soundautor")
						EnableSFXRelease = DrawTick(x + 310 * MenuScale, y + MenuScale, EnableSFXRelease)
						If EnableSFXRelease_Prev% <> EnableSFXRelease
							If EnableSFXRelease%
								For snd.Sound = Each Sound
									For i=0 To 31
										If snd\channels[i]<>0 Then
											If ChannelPlaying(snd\channels[i]) Then
												StopChannel(snd\channels[i])
											EndIf
										EndIf
									Next
									If snd\internalHandle<>0 Then
										FreeSound snd\internalHandle
										snd\internalHandle = 0
									EndIf
									snd\releaseTime = 0
								Next
							Else
								For snd.Sound = Each Sound
									If snd\internalHandle = 0 Then snd\internalHandle = LoadSound(snd\name)
								Next
							EndIf
							EnableSFXRelease_Prev% = EnableSFXRelease
						EndIf
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th+220*MenuScale,"sfxautorelease")
						EndIf
						y = y + 30*MenuScale
						
						Color 255,255,255
						Text x + 20 * MenuScale, y, GetLocalString("Options", "usertracks")
						EnableUserTracks = DrawTick(x + 310 * MenuScale, y + MenuScale, EnableUserTracks)
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th,"usertrack")
						EndIf
						
						If PrevEnableUserTracks
							y = y + 30 * MenuScale
							Color 255,255,255
							Text x + 20 * MenuScale, y, GetLocalString("Options", "usertrackm")
							UserTrackMode = DrawTick(x + 310 * MenuScale, y + MenuScale, UserTrackMode)
							If UserTrackMode
								Text x + 350 * MenuScale, y + MenuScale, GetLocalString("Options", "usertrackrepeat")
							Else
								Text x + 350 * MenuScale, y + MenuScale, GetLocalString("Options", "usertrackrandom")
							EndIf
							If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
								DrawOptionsTooltip(tx,ty,tw,th,"usertrackmode")
							EndIf
							If DrawButton(x + 20 * MenuScale, y + 30 * MenuScale, 250 * MenuScale, 25 * MenuScale, GetLocalString("Options", "usertrackscan"), False)
								DebugLog "User Tracks Check Started"
								
								UserTrackCheck% = 0
								UserTrackCheck2% = 0
								
								Dir=ReadDir("SFX\Radio\UserTracks\")
								Repeat
									file$=NextFile(Dir)
									If file$="" Then Exit
									If FileType("SFX\Radio\UserTracks\"+file$) = 1 Then
										UserTrackCheck = UserTrackCheck + 1
										test = LoadSound("SFX\Radio\UserTracks\"+file$)
										If test<>0
											UserTrackCheck2 = UserTrackCheck2 + 1
										EndIf
										FreeSound test
									EndIf
								Forever
								CloseDir Dir
								
								DebugLog "User Tracks Check Ended"
							EndIf
							If MouseOn(x+20*MenuScale,y+30*MenuScale,190*MenuScale,25*MenuScale)
								DrawOptionsTooltip(tx,ty,tw,th,"usertrackscan")
							EndIf
							If UserTrackCheck%>0
								Text x + 20 * MenuScale, y + 100 * MenuScale, "User tracks found ("+UserTrackCheck2+"/"+UserTrackCheck+" successfully loaded)"
							EndIf
						Else
							UserTrackCheck%=0
						EndIf
						
					Case 6 ;Controls

						height = 275 * MenuScale
						DrawFrame(x, y, width, height)	
						
						y = y + 20*MenuScale
						
						MouseSens = (SlideBar(x + 310*MenuScale, y-4*MenuScale, 150*MenuScale, (MouseSens+0.5)*100.0)/100.0)-0.5
						Color(255, 255, 255)
						Text(x + 20 * MenuScale, y, GetLocalString("Options", "sensitivity"))
						If MouseOn(x+310*MenuScale,y-4*MenuScale,150*MenuScale+14,20)
							DrawOptionsTooltip(tx,ty,tw,th,"mousesensitivity",MouseSens)
						EndIf
						
						y = y + 40*MenuScale
						
						Color(255, 255, 255)
						Text(x + 20 * MenuScale, y, GetLocalString("Options", "invert"))
						InvertMouse = DrawTick(x + 310 * MenuScale, y + MenuScale, InvertMouse)
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th,"mouseinvert")
						EndIf
						
						y = y + 40*MenuScale
						
						MouseSmooth = (SlideBar(x + 310*MenuScale, y-4*MenuScale, 150*MenuScale, (MouseSmooth)*50.0)/50.0)
						Color(255, 255, 255)
						Text(x + 20 * MenuScale, y, GetLocalString("Options", "smooth"))
						If MouseOn(x+310*MenuScale,y-4*MenuScale,150*MenuScale+14,20)
							DrawOptionsTooltip(tx,ty,tw,th,"mousesmoothing",MouseSmooth)
						EndIf
						
						Color(255, 255, 255)
						
						y = y + 30*MenuScale
						Text(x + 20 * MenuScale, y, GetLocalString("Options", "keys"))
						y = y + 10*MenuScale
						
						Text(x + 20 * MenuScale, y + 20 * MenuScale, GetLocalString("Options", "forward"))
						InputBox(x + 160 * MenuScale, y + 20 * MenuScale,100*MenuScale,20*MenuScale,I_Keys\KeyName[Min(I_Keys\UP,210)],5)
						Text(x + 20 * MenuScale, y + 40 * MenuScale, GetLocalString("Options", "left"))
						InputBox(x + 160 * MenuScale, y + 40 * MenuScale,100*MenuScale,20*MenuScale,I_Keys\KeyName[Min(I_Keys\LEFT,210)],3)
						Text(x + 20 * MenuScale, y + 60 * MenuScale, GetLocalString("Options", "backward"))
						InputBox(x + 160 * MenuScale, y + 60 * MenuScale,100*MenuScale,20*MenuScale,I_Keys\KeyName[Min(I_Keys\DOWN,210)],6)
						Text(x + 20 * MenuScale, y + 80 * MenuScale, GetLocalString("Options", "right"))
						InputBox(x + 160 * MenuScale, y + 80 * MenuScale,100*MenuScale,20*MenuScale,I_Keys\KeyName[Min(I_Keys\RIGHT,210)],4)
						Text(x + 20 * MenuScale, y + 100 * MenuScale, GetLocalString("Options", "save"))
						InputBox(x + 160 * MenuScale, y + 100 * MenuScale,100*MenuScale,20*MenuScale,I_Keys\KeyName[Min(I_Keys\SAVE,210)],11)
						
						Text(x + 280 * MenuScale, y + 20 * MenuScale, GetLocalString("Options", "blink"))
						InputBox(x + 470 * MenuScale, y + 20 * MenuScale,100*MenuScale,20*MenuScale,I_Keys\KeyName[Min(I_Keys\BLINK,210)],7)				
						Text(x + 280 * MenuScale, y + 40 * MenuScale, GetLocalString("Options", "sprint"))
						InputBox(x + 470 * MenuScale, y + 40 * MenuScale,100*MenuScale,20*MenuScale,I_Keys\KeyName[Min(I_Keys\SPRINT,210)],8)
						Text(x + 280 * MenuScale, y + 60 * MenuScale, GetLocalString("Options", "inv"))
						InputBox(x + 470 * MenuScale, y + 60 * MenuScale,100*MenuScale,20*MenuScale,I_Keys\KeyName[Min(I_Keys\INV,210)],9)
						Text(x + 280 * MenuScale, y + 80 * MenuScale, GetLocalString("Options", "crouch"))
						InputBox(x + 470 * MenuScale, y + 80 * MenuScale,100*MenuScale,20*MenuScale,I_Keys\KeyName[Min(I_Keys\CROUCH,210)],10)	
						Text(x + 280 * MenuScale, y + 100 * MenuScale, GetLocalString("Options", "console"))
						InputBox(x + 470 * MenuScale, y + 100 * MenuScale,100*MenuScale,20*MenuScale,I_Keys\KeyName[Min(I_Keys\CONSOLE,210)],12)
						
						If MouseOn(x+20*MenuScale,y,width-40*MenuScale,120*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th,"controls")
						EndIf
						
						For i = 0 To 227
							If KeyHit(i) Then key = i : Exit
						Next
						If key<>0 Then
							Select SelectedInputBox
								Case 3
									I_Keys\LEFT = key
								Case 4
									I_Keys\RIGHT = key
								Case 5
									I_Keys\UP = key
								Case 6
									I_Keys\DOWN = key
								Case 7
									I_Keys\BLINK = key
								Case 8
									I_Keys\SPRINT = key
								Case 9
									I_Keys\INV = key
								Case 10
									I_Keys\CROUCH = key
								Case 11
									I_Keys\SAVE = key
								Case 12
									I_Keys\CONSOLE = key
							End Select
							SelectedInputBox = 0
						EndIf
						
					Case 7 ;Advanced
					
						Local PrevFramelimit% = Framelimit

						If PrevFramelimit Then
							height = 380 * MenuScale
						Else
							height = 350 * MenuScale
						EndIf
						
						DrawFrame(x, y, width, height)	
						
						y = y + 20*MenuScale
						
						Color 255,255,255				
						Text(x + 20 * MenuScale, y, GetLocalString("Options", "hud"))	
						HUDenabled = DrawTick(x + 310 * MenuScale, y + MenuScale, HUDenabled)
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th,"hud")
						EndIf
						
						y=y+30*MenuScale
						
						Color 255,255,255
						Text(x + 20 * MenuScale, y, GetLocalString("Options", "aconsole"))
						I_Opt\ConsoleEnabled = DrawTick(x + 310 * MenuScale, y + MenuScale, I_Opt\ConsoleEnabled)
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th,"consoleenable")
						EndIf
						
						y = y + 30*MenuScale
						
						Color 255,255,255
						Text(x + 20 * MenuScale, y, GetLocalString("Options", "errconsole"))
						I_Opt\ConsoleOnError = DrawTick(x + 310 * MenuScale, y + MenuScale, I_Opt\ConsoleOnError)
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th,"consoleerror")
						EndIf
						
						y = y + 50*MenuScale
						
						Color 255,255,255
						Text(x + 20 * MenuScale, y, GetLocalString("Options", "achpop"))
						AchvMSGenabled% = DrawTick(x + 310 * MenuScale, y + MenuScale, AchvMSGenabled%)
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th,"achpopup")
						EndIf
						
						y = y + 50*MenuScale
						
						Color 255,255,255
						Text(x + 20 * MenuScale, y, GetLocalString("Options", "fps"))
						I_Opt\ShowFPS% = DrawTick(x + 310 * MenuScale, y + MenuScale, I_Opt\ShowFPS)
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th,"showfps")
						EndIf
						
						y = y + 30*MenuScale
						
						Color 255,255,255
						Text(x + 20 * MenuScale, y, GetLocalString("Options", "fpslimit"))
						Color 255,255,255
						If DrawTick(x + 310 * MenuScale, y, PrevFramelimit > 0) Then
							If PrevFramelimit Then
								FrameLimit = 20+SlideBar(x + 150*MenuScale, y+30*MenuScale, 100*MenuScale, Framelimit-20)
								Color 255,255,0
								Text(x + 25 * MenuScale, y + 25 * MenuScale, Framelimit+" FPS")
							Else
								Framelimit = 60
							EndIf
						Else
							Framelimit = 0
						EndIf
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale) Lor (MouseOn(x+150*MenuScale,y+30*MenuScale,100*MenuScale+14,20) And PrevFramelimit > 0) Then
							DrawOptionsTooltip(tx,ty,tw,th,"framelimit",PrevFramelimit > 0)
						EndIf
						
						If PrevFramelimit Then
							y = y + 80*MenuScale
						Else
							y = y + 50*MenuScale
						EndIf
						
						Color 255,255,255
						
						Text(x + 20 * MenuScale, y, GetLocalString("Options", "launcher"))
						I_Opt\LauncherEnabled = DrawTick(x + 310 * MenuScale, y + MenuScale, I_Opt\LauncherEnabled)
						If MouseOn(x+310*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th,"launcher")
						EndIf
						
						y = y + 50*MenuScale
						
						If DrawButton(x + 20 * MenuScale, y, 250 * MenuScale, 25 * MenuScale, GetLocalString("Options", "resetall"), False)
							DeleteFile(OptionFile)
							DefaultOptionsINI()
							Brightness = 0
							ScreenGamma = 1.0
							I_Opt\ShowFPS = 0
							Framelimit = 0
							Vsync = 1
							MouseSens = 0.0
							InvertMouse = 0
							MouseSmooth = 1.0
							CameraFogNear = 0.5
							CameraFogFar = 6.0
							CameraFogColor (Camera, 0, 0, 0)
							AchvMSGenabled = 1
							BumpEnabled = 1
							AntiAlias = 1
							HUDenabled = 1
							IntroEnabled = 1
							EnableRoomLights = 1
							TextureDetails = 3
							TextureFloat# = -0.4 ;If default TextureDetails changes, this must too
							ParticleAmount = 2
							FOV = 60
							EnableVRam = 0
							MusicVolume = 0.5
							SFXVolume = 1.0
							EnableUserTracks = 1
							UserTrackMode = 1
							EnableSFXRelease = 1
							I_Keys\RIGHT = 32
							I_Keys\LEFT = 30
							I_Keys\UP = 17
							I_Keys\DOWN = 31
							I_Keys\BLINK = 57
							I_Keys\SPRINT = 42
							I_Keys\INV = 15
							I_Keys\CROUCH = 29
							I_Keys\SAVE = 63
							I_Keys\CONSOLE = 61
							I_Opt\LauncherEnabled = 0
							I_Opt\ConsoleEnabled = 0
							I_Opt\ConsoleOnError = 0
						EndIf
					
				End Select

		End Select
		
	EndIf
	
	Color 255,255,255
	SetFont I_Opt\Fonts[0]
	Text 10, I_Opt\GraphicHeight-40, "v"+VersionNumber
	
	;DrawTiledImageRect(MenuBack, 985 * MenuScale, 860 * MenuScale, 200 * MenuScale, 20 * MenuScale, 1200 * MenuScale, 866 * MenuScale, 300, 20 * MenuScale)
	
	If I_Opt\GraphicMode = 0 Then DrawImage CursorIMG, ScaledMouseX(),ScaledMouseY()
	
	SetFont I_Opt\Fonts[1]
End Function

Const L_WIDTH = 640
Const L_HEIGHT = 480

Function UpdateLauncher(I_LOpt.LauncherOptions)
	
	MenuScale = 1
	
	Graphics3DExt(L_WIDTH, L_HEIGHT, 2)

	;InitExt
	
	SetBuffer BackBuffer()
	
	RealGraphicWidth = I_Opt\GraphicWidth
	RealGraphicHeight = I_Opt\GraphicHeight
	
	I_Opt\Fonts[1] = LoadLocalFont("Font1", 1)
	SetFont I_Opt\Fonts[1]
	MenuWhite = LoadImage_Strict("GFX\menu\menuwhite.jpg")
	MenuBlack = LoadImage_Strict("GFX\menu\menublack.jpg")
	MaskImage MenuBlack, 255,255,0
	
	Local IMG% = LoadImage_Strict("GFX\menu\launcher.jpg")
	
	Local i%	
	
	For i = 0 To 3
		ArrowIMG[i] = LoadImage_Strict("GFX\menu\arrow.png")
		RotateImage(ArrowIMG[i], 90 * i)
		HandleImage(ArrowIMG[i], 0, 0)
	Next
	
	For i% = 1 To I_LOpt\TotalGFXModes
		Local samefound% = False
		For  n% = 0 To I_LOpt\TotalGFXModes - 1
			If I_LOpt\GfxModeWidths[n] = GfxModeWidth(i) And I_LOpt\GfxModeHeights[n] = GfxModeHeight(i) Then samefound = True : Exit
		Next
		If samefound = False Then
			If I_Opt\GraphicWidth = GfxModeWidth(i) And I_Opt\GraphicHeight = GfxModeHeight(i) Then I_LOpt\SelectedGFXMode = I_LOpt\GFXModes
			I_LOpt\GfxModeWidths[I_LOpt\GFXModes] = GfxModeWidth(i)
			I_LOpt\GfxModeHeights[I_LOpt\GFXModes] = GfxModeHeight(i)
			I_LOpt\GFXModes=I_LOpt\GFXModes+1 
		EndIf
	Next
	
	Local Dir% = ReadDir("Localization\")
	Local File$
	NextFile$(Dir) : NextFile$(Dir)
	Repeat
		File = NextFile$(Dir)
		If File = "" Then Exit
		If FileType("Localization\" + File) = 2 Then
			If "Localization\" + File + "\" = I_Loc\LangPath Then
				I_LOpt\SelectedLoc = j
			EndIf
			I_LOpt\Locs[j] = File
			j = j + 1
		EndIf
	Forever
	CloseDir Dir
	
	BlinkMeterIMG% = LoadImage_Strict("GFX\blinkmeter.jpg")
	
	Local quit% = False
	
	Repeat
		
		Cls
		
		MouseHit1 = MouseHit(1)
		
		Color 255, 255, 255
		DrawImage(IMG, 0, 0)
		
		Text(20, 240 - 65, GetLocalString("Launcher", "resolution"))
		
		Local x% = 25
		Local y% = 200
		
		Select I_Opt\GraphicMode
			Case 0
				Text(x, y, I_LOpt\GfxModeWidths[I_LOpt\SelectedGFXMode] + "x" + I_LOpt\GfxModeHeights[I_LOpt\SelectedGFXMode])
			Case 1
				Text(x, y, I_LOpt\GfxModeWidths[I_LOpt\SelectedGFXMode] + "x" + I_LOpt\GfxModeHeights[I_LOpt\SelectedGFXMode])
				If I_LOpt\GfxModeWidths[I_LOpt\SelectedGFXMode]<DesktopWidth() Then
					Text(x, y + 15, "(" + GetLocalString("Launcher", "upscale"))
					Text(x, y + 30, DesktopWidth() + "x" + DesktopHeight())
				ElseIf I_LOpt\GfxModeWidths[I_LOpt\SelectedGFXMode]>DesktopWidth() Then
					Text(x, y + 15, "(" + GetLocalString("Launcher", "downscale"))
					Text(x, y + 30, DesktopWidth() + "x" + DesktopHeight())
				EndIf
			Case 2
				Text(x, y, I_LOpt\GfxModeWidths[I_LOpt\SelectedGFXMode] + "x" + I_LOpt\GfxModeHeights[I_LOpt\SelectedGFXMode])
		End Select
		
		If DrawButton(x, y + 50, 100, 30, "+", False, False, False) Then I_LOpt\SelectedGFXMode = Min(I_LOpt\SelectedGFXMode + 1, I_LOpt\GFXModes - 1)
		If DrawButton(x + 150, y + 50, 100, 30, "-", False, False, False) Then I_LOpt\SelectedGFXMode = Max(I_LOpt\SelectedGFXMode - 1, 0)
		;-----------------------------------------------------------------
		
		y = y + 100
		
		If DrawButton(x + 100, y, 150, 30, GetLocalString("Launcher", "cycle"), False, False, False) Then
			I_LOpt\SelectedLoc = (I_LOpt\SelectedLoc + 1) Mod I_LOpt\TotalLocs
			UpdateLang(I_LOpt\Locs[I_LOpt\SelectedLoc])
		EndIf
		
		Text(x, y + 5, I_LOpt\Locs[I_LOpt\SelectedLoc])
		
		;-----------------------------------------------------------------
		
		I_Opt\LauncherEnabled = DrawTick(40 + 430 - 15, 260 - 55 + 95 + 8, I_Opt\LauncherEnabled)
		
		;0 = Full, 1 = Borderless, 2 = Windowed
		Text(40 + 430 + 15, 262 - 55, GetLocalString("Launcher", "mode" + I_Opt\GraphicMode))
		
		If DrawButton(40 + 430 - 15, 260 - 55 + 50, 100, 30, "Nice", False, False, False) Then
			I_Opt\GraphicMode = (I_Opt\GraphicMode + 1) Mod 3
		EndIf
		
		Color 255, 255, 255
		Text(40 + 430 + 15, 262 - 55 + 95 + 8, GetLocalString("Launcher", "uselauncher"))
		
		If DrawButton(L_WIDTH - 30 - 90, L_HEIGHT - 50 - 55, 100, 30, GetLocalString("Launcher", "launch"), False, False, False) Then
			I_Opt\GraphicWidth = I_LOpt\GfxModeWidths[I_LOpt\SelectedGFXMode]
			I_Opt\GraphicHeight = I_LOpt\GfxModeHeights[I_LOpt\SelectedGFXMode]
			RealGraphicWidth = I_Opt\GraphicWidth
			RealGraphicHeight = I_Opt\GraphicHeight
			Exit
		EndIf
		
		If DrawButton(L_WIDTH - 30 - 90, L_HEIGHT - 50, 100, 30, GetLocalString("Launcher", "exit"), False, False, False) Then quit = True : Exit
		Flip
	Forever
	
	PutINIValue(OptionFile, "options", "width", I_LOpt\GfxModeWidths[I_LOpt\SelectedGFXMode])
	PutINIValue(OptionFile, "options", "height", I_LOpt\GfxModeHeights[I_LOpt\SelectedGFXMode])
	PutINIValue(OptionFile, "options", "mode", I_Opt\GraphicMode)
	PutINIValue(OptionFile, "options", "launcher enabled", I_Opt\LauncherEnabled)
	PutINIValue(OptionFile, "options", "pack", I_LOpt\Locs[I_LOpt\SelectedLoc])
	
	If quit Then End
	
	FreeImage(IMG)
	
End Function


Function DrawTiledImageRect(img%, srcX%, srcY%, srcwidth#, srcheight#, x%, y%, width%, height%)
	
	Local x2% = x
	While x2 < x+width
		Local y2% = y
		While y2 < y+height
			If x2 + srcwidth > x + width Then srcwidth = srcwidth - Max((x2 + srcwidth) - (x + width), 1)
			If y2 + srcheight > y + height Then srcheight = srcheight - Max((y2 + srcheight) - (y + height), 1)
			DrawImageRect(img, x2, y2, srcX, srcY, srcwidth, srcheight)
			y2 = y2 + srcheight
		Wend
		x2 = x2 + srcwidth
	Wend
	
End Function



Type LoadingScreens
	Field imgpath$
	Field img%
	Field ID%
	Field title$
	Field alignx%, aligny%
	Field disablebackground%
	Field txt$[5], txtamount%
End Type

Function InitLoadingScreens(file$)
	If I_Loc\Localized And FileType(I_Loc\LangPath + file$)=1 Then
		file = I_Loc\LangPath + file
	EndIf
	Local TemporaryString$, i%
	Local ls.LoadingScreens
	
	Local f = OpenFile(file)
	
	While Not Eof(f)
		TemporaryString = Trim(ReadLine(f))
		If Left(TemporaryString,1) = "[" Then
			TemporaryString = Mid(TemporaryString, 2, Len(TemporaryString) - 2)
			
			ls.LoadingScreens = New LoadingScreens
			LoadingScreenAmount=LoadingScreenAmount+1
			ls\ID = LoadingScreenAmount
			
			ls\title = TemporaryString
			ls\imgpath = GetINIString(file, TemporaryString, "image path")
			
			For i = 0 To 4
				ls\txt[i] = GetINIString(file, TemporaryString, "text"+(i+1))
				If ls\txt[i]<> "" Then ls\txtamount=ls\txtamount+1
			Next
			
			ls\disablebackground = GetINIInt(file, TemporaryString, "disablebackground")
			
			Select Lower(GetINIString(file, TemporaryString, "align x"))
				Case "left"
					ls\alignx = -1
				Case "middle", "center"
					ls\alignx = 0
				Case "right" 
					ls\alignx = 1
			End Select 
			
			Select Lower(GetINIString(file, TemporaryString, "align y"))
				Case "top", "up"
					ls\aligny = -1
				Case "middle", "center"
					ls\aligny = 0
				Case "bottom", "down"
					ls\aligny = 1
			End Select 			
			
		EndIf
	Wend
	
	CloseFile f
End Function



Function DrawLoading(percent%, shortloading=False)
	
	Local x%, y%
	
	If percent = 0 Then
		LoadingScreenText=0
		
		temp = Rand(1,LoadingScreenAmount)
		For ls.loadingscreens = Each LoadingScreens
			If ls\id = temp Then
				If ls\img=0 Then
					Cls
					Flip False
					ls\img = LoadImage_Strict("Loadingscreens\"+ls\imgpath)
					If LoadingScreenScale <> 1.0 Then ls\img = ResizeImage2(ls\img, LoadingScreenScale * ImageWidth(ls\img), LoadingScreenScale * ImageHeight(ls\img))
				EndIf
				SelectedLoadingScreen = ls 
				Exit
			EndIf
		Next
	EndIf
	
	firstloop = True
	Repeat 
		
		ClsColor 0,0,0
		Cls
		
		If percent > 20 Then
			UpdateMusic()
		EndIf
		
		If shortloading = False Then
			If percent > (100.0 / SelectedLoadingScreen\txtamount)*(LoadingScreenText+1) Then
				LoadingScreenText=LoadingScreenText+1
			EndIf
		EndIf
		
		If (Not SelectedLoadingScreen\disablebackground) Then
			DrawImage LoadingBack, I_Opt\GraphicWidth/2 - ImageWidth(LoadingBack)/2, I_Opt\GraphicHeight/2 - ImageHeight(LoadingBack)/2
		EndIf	
		
		If SelectedLoadingScreen\alignx = 0 Then
			x = I_Opt\GraphicWidth/2 - ImageWidth(SelectedLoadingScreen\img)/2 
		ElseIf  SelectedLoadingScreen\alignx = 1
			x = I_Opt\GraphicWidth - ImageWidth(SelectedLoadingScreen\img)
		Else
			x = 0
		EndIf
		
		If SelectedLoadingScreen\aligny = 0 Then
			y = I_Opt\GraphicHeight/2 - ImageHeight(SelectedLoadingScreen\img)/2 
		ElseIf  SelectedLoadingScreen\aligny = 1
			y = I_Opt\GraphicHeight - ImageHeight(SelectedLoadingScreen\img)
		Else
			y = 0
		EndIf
		
		DrawImage SelectedLoadingScreen\img, x, y
		
		Local width% = 300, height% = 20
		x% = I_Opt\GraphicWidth / 2 - width / 2
		y% = I_Opt\GraphicHeight / 2 + 30 - 100
		
		Rect(x, y, width+4, height, False)
		For  i% = 1 To Int((width - 2) * (percent / 100.0) / 10)
			DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
		Next
		
		If SelectedLoadingScreen\title = "CWM" Then
			
			If Not shortloading Then 
				If firstloop Then 
					If percent = 0 Then
						PlaySound_Strict LoadTempSound("SFX\SCP\990\cwm1.cwm")
					ElseIf percent = 100
						PlaySound_Strict LoadTempSound("SFX\SCP\990\cwm2.cwm")
					EndIf
				EndIf
			EndIf
			
			SetFont I_Opt\Fonts[2]
			strtemp$ = ""
			temp = Rand(2,9)
			For i = 0 To temp
				strtemp$ = STRTEMP + Chr(Rand(48,122))
			Next
			Text(I_Opt\GraphicWidth / 2, I_Opt\GraphicHeight / 2 + 80, strtemp, True, True)
			
			If percent = 0 Then 
				If Rand(5)=1 Then
					Select Rand(2)
						Case 1
							SelectedLoadingScreen\txt[0] = GetLocalStringR("990", "date", CurrentDate())
						Case 2
							SelectedLoadingScreen\txt[0] = CurrentTime()
					End Select
				Else
					Select Rand(13)
						Case 1
							SelectedLoadingScreen\txt[0] = GetLocalString("990", "radio")
						Case 2
							SelectedLoadingScreen\txt[0] = GetLocalString("990", "burn")
						Case 3
							SelectedLoadingScreen\txt[0] = GetLocalString("990", "control")
						Case 4
							SelectedLoadingScreen\txt[0] = GetLocalString("990", "e")
						Case 5
							SelectedLoadingScreen\txt[0] = GetLocalString("990", "trust")
						Case 6 
							SelectedLoadingScreen\txt[0] = GetLocalString("990", "look")
						Case 7
							SelectedLoadingScreen\txt[0] = GetLocalString("990", "q")
						Case 8, 9
							SelectedLoadingScreen\txt[0] = GetLocalString("990", "jorge")
						Case 10
							SelectedLoadingScreen\txt[0] = GetLocalString("990", "q2")
						Case 11
							SelectedLoadingScreen\txt[0] = GetLocalString("990", "midnight")
						Case 12
							SelectedLoadingScreen\txt[0] = GetLocalString("990", "coming")
						Case 13
							SelectedLoadingScreen\txt[0] = GetLocalString("990", "alloy")
					End Select
				EndIf
			EndIf
			
			strtemp$ = SelectedLoadingScreen\txt[0]
			temp = Int(Len(SelectedLoadingScreen\txt[0])-Rand(5))
			For i = 0 To Rand(10,15);temp
				strtemp$ = Replace(SelectedLoadingScreen\txt[0],Mid(SelectedLoadingScreen\txt[0],Rand(1,Len(strtemp)-1),1),Chr(Rand(130,250)))
			Next		
			SetFont I_Opt\Fonts[1]
			RowText(strtemp, I_Opt\GraphicWidth / 2-200, I_Opt\GraphicHeight / 2 +120,400,300,True)		
		Else
			
			Color 0,0,0
			SetFont I_Opt\Fonts[2]
			Text(I_Opt\GraphicWidth / 2 + 1, I_Opt\GraphicHeight / 2 + 80 + 1, SelectedLoadingScreen\title, True, True)
			SetFont I_Opt\Fonts[1]
			RowText(SelectedLoadingScreen\txt[LoadingScreenText], I_Opt\GraphicWidth / 2-200+1, I_Opt\GraphicHeight / 2 +120+1,400,300,True)
			
			Color 255,255,255
			SetFont I_Opt\Fonts[2]
			Text(I_Opt\GraphicWidth / 2, I_Opt\GraphicHeight / 2 +80, SelectedLoadingScreen\title, True, True)
			SetFont I_Opt\Fonts[1]
			RowText(SelectedLoadingScreen\txt[LoadingScreenText], I_Opt\GraphicWidth / 2-200, I_Opt\GraphicHeight / 2 +120,400,300,True)
			
		EndIf
		
		Color 0,0,0
		Text(I_Opt\GraphicWidth / 2 + 1, I_Opt\GraphicHeight / 2 - 100 + 1, GetLocalString("Menu", "loading") + " - " + percent + " %", True, True)
		Color 255,255,255
		Text(I_Opt\GraphicWidth / 2, I_Opt\GraphicHeight / 2 - 100, GetLocalString("Menu", "loading") + " - " + percent + " %", True, True)
		
		If percent = 100 Then 
			If firstloop And SelectedLoadingScreen\title <> "CWM" Then PlaySound_Strict LoadTempSound(("SFX\Horror\Horror8.ogg"))
			Text(I_Opt\GraphicWidth / 2, I_Opt\GraphicHeight - 50, GetLocalString("Menu", "pressany"), True, True)
		Else
			FlushKeys()
			FlushMouse()
		EndIf
		
		If I_Opt\GraphicMode = 1 Then
			If (RealGraphicWidth<>I_Opt\GraphicWidth) Lor (RealGraphicHeight<>I_Opt\GraphicHeight) Then
				SetBuffer TextureBuffer(fresize_texture)
				ClsColor 0,0,0 : Cls
				CopyRect 0,0,I_Opt\GraphicWidth,I_Opt\GraphicHeight,1024-I_Opt\GraphicWidth/2,1024-I_Opt\GraphicHeight/2,BackBuffer(),TextureBuffer(fresize_texture)
				SetBuffer BackBuffer()
				ClsColor 0,0,0 : Cls
				ScaleRender(0,0,2050.0 / Float(I_Opt\GraphicWidth) * AspectRatioRatio, 2050.0 / Float(I_Opt\GraphicWidth) * AspectRatioRatio)
				;might want to replace Float(I_Opt\GraphicWidth) with Max(I_Opt\GraphicWidth,I_Opt\GraphicHeight) if portrait sizes cause issues
				;everyone uses landscape so it's probably a non-issue
			EndIf
		EndIf
		
		;not by any means a perfect solution
		;Not even proper gamma correction but it's a nice looking alternative that works in windowed mode
		If ScreenGamma>1.0 Then
			CopyRect 0,0,RealGraphicWidth,RealGraphicHeight,1024-RealGraphicWidth/2,1024-RealGraphicHeight/2,BackBuffer(),TextureBuffer(fresize_texture)
			EntityBlend fresize_image,1
			ClsColor 0,0,0 : Cls
			ScaleRender(-1.0/Float(RealGraphicWidth),1.0/Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth))
			EntityFX fresize_image,1+32
			EntityBlend fresize_image,3
			EntityAlpha fresize_image,ScreenGamma-1.0
			ScaleRender(-1.0/Float(RealGraphicWidth),1.0/Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth))
		ElseIf ScreenGamma<1.0 Then
			CopyRect 0,0,RealGraphicWidth,RealGraphicHeight,1024-RealGraphicWidth/2,1024-RealGraphicHeight/2,BackBuffer(),TextureBuffer(fresize_texture)
			EntityBlend fresize_image,1
			ClsColor 0,0,0 : Cls
			ScaleRender(-1.0/Float(RealGraphicWidth),1.0/Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth))
			EntityFX fresize_image,1+32
			EntityBlend fresize_image,2
			EntityAlpha fresize_image,1.0
			SetBuffer TextureBuffer(fresize_texture2)
			ClsColor 255*ScreenGamma,255*ScreenGamma,255*ScreenGamma
			Cls
			SetBuffer BackBuffer()
			ScaleRender(-1.0/Float(RealGraphicWidth),1.0/Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth),2048.0 / Float(RealGraphicWidth))
			SetBuffer(TextureBuffer(fresize_texture2))
			ClsColor 0,0,0
			Cls
			SetBuffer(BackBuffer())
		EndIf
		EntityFX fresize_image,1
		EntityBlend fresize_image,1
		EntityAlpha fresize_image,1.0
		
		Flip False
		
		firstloop = False
		If percent <> 100 Then Exit
		
	Until (GetKey()<>0 Lor MouseHit(1))
End Function

Function rInput$(aString$, MaxChr%)
	Local value% = GetKey()
	Local length% = Len(aString)
	
	If CursorPos = -1 Then CursorPos = length
	
	If KeyDown(29) Then
		If value = 30 Then CursorPos = length
		If value = 31 Then CursorPos = 0
		If value = 22 Then
			aString = Left(aString, CursorPos) + GetClipboardContents() + Right(aString, length - CursorPos)
			CursorPos = CursorPos + Len(aString) - length
			If MaxChr > 0 And MaxChr < Len(aString) Then aString = Left(aString, MaxChr) : CursorPos = MaxChr
		EndIf
		Return aString
	EndIf
	
	If value = 30
		CursorPos = Min(CursorPos + 1, length)
	ElseIf value = 31
		CursorPos = Max(CursorPos - 1, 0)
	Else
		aString = TextInput(Left(aString, CursorPos)) + Mid(aString, CursorPos+1)
		CursorPos = CursorPos + Len(aString) - length
		If MaxChr > 0 And MaxChr < Len(aString) Then aString = Left(aString, MaxChr) : CursorPos = MaxChr
	EndIf
	Return aString
	
End Function

Function InputBox$(x%, y%, width%, height%, Txt$, ID% = 0, MaxChr% = 0)
	;TextBox(x,y,width,height,Txt$)
	Color (255, 255, 255)
	DrawTiledImageRect(MenuWhite, (x Mod 256), (y Mod 256), 512, 512, x, y, width, height)
	;Rect(x, y, width, height)
	Color (0, 0, 0)
	
	Local MouseOnBox% = False
	If MouseOn(x, y, width, height) Then
		Color(50, 50, 50)
		MouseOnBox = True
		If MouseHit1 And SelectedInputBox <> ID Then SelectedInputBox = ID : FlushKeys : CursorPos = -1
	EndIf
	
	Rect(x + 2, y + 2, width - 4, height - 4)
	Color (255, 255, 255)	
	
	If (Not MouseOnBox) And MouseHit1 And SelectedInputBox = ID Then SelectedInputBox = 0 : CursorPos = -1
	
	If SelectedInputBox = ID Then
		Txt = rInput(Txt, MaxChr)
		If (MilliSecs() Mod 800) < 400 Then Rect (x + width / 2 - (StringWidth(Txt)) / 2 + StringWidth(Left(Txt, CursorPos)), y + height / 2 - 5, 2, 12)
	EndIf	
	
	Text(x + width / 2, y + height / 2, Txt, True, True)
	
	Return Txt
End Function

Function DrawFrame(x%, y%, width%, height%, xoffset%=0, yoffset%=0)
	Color 255, 255, 255
	DrawTiledImageRect(MenuWhite, xoffset, (y Mod 256), 512, 512, x, y, width, height)
	
	DrawTiledImageRect(MenuBlack, yoffset, (y Mod 256), 512, 512, x+3*MenuScale, y+3*MenuScale, width-6*MenuScale, height-6*MenuScale)	
End Function

Function DrawButton%(x%, y%, width%, height%, txt$, bigfont% = True, waitForMouseUp%=False, usingAA%=True)

	Local clicked% = False
	DrawFrame (x, y, width, height)
	If MouseOn(x, y, width, height) Then
		Color(30, 30, 30)
		If (MouseHit1 And (Not waitForMouseUp)) Lor (MouseUp1 And waitForMouseUp) Then 
			clicked = True
			PlaySound_Strict(ButtonSFX)
		EndIf
		Rect(x + 4, y + 4, width - 8, height - 8)	
	Else
		Color(0, 0, 0)
	EndIf
	
	Color (255, 255, 255)
	
	If bigfont Then SetFont I_Opt\Fonts[2] Else SetFont I_Opt\Fonts[1]
	Text(x + width / 2, y + height / 2, txt, True, True)
	
	Return clicked
End Function

Function DrawTick%(x%, y%, selected%, locked% = False)
	Local width% = 20 * MenuScale, height% = 20 * MenuScale
	
	Color (255, 255, 255)
	DrawTiledImageRect(MenuWhite, (x Mod 256), (y Mod 256), 512, 512, x, y, width, height)
	
	Local Highlight% = MouseOn(x, y, width, height)
	
	If Highlight Then
		If locked Then
			Color(0, 0, 0)
			If MouseHit1 Then PlaySound_Strict (ButtonSFX2)
		Else
			Color(50, 50, 50)
			If MouseHit1 Then selected = (Not selected) : PlaySound_Strict (ButtonSFX)
		EndIf
	Else
		Color(0, 0, 0)
	EndIf
	
	Rect(x + 2, y + 2, width - 4, height - 4)
	
	If selected Then
		If Highlight Then
			Color 255,255,255
		Else
			Color 200,200,200
		EndIf
		DrawTiledImageRect(MenuWhite, (x Mod 256), (y Mod 256), 512, 512, x + 4, y + 4, width - 8, height - 8)
		;Rect(x + 4, y + 4, width - 8, height - 8)
	EndIf
	
	Color 255, 255, 255
	
	Return selected
End Function

Function SlideBar#(x%, y%, width%, value#)
	
	If MouseDown1 And OnSliderID=0 Then
		If ScaledMouseX() >= x And ScaledMouseX() <= x + width + 14 And ScaledMouseY() >= y And ScaledMouseY() <= y + 20 Then
			value = Min(Max((ScaledMouseX() - x) * 100 / width, 0), 100)
		EndIf
	EndIf
	
	Color 255,255,255
	Rect(x, y, width + 14, 20,False)
	
	DrawImage(BlinkMeterIMG, x + width * value / 100.0 +3, y+3)
	
	Color 170,170,170 
	Text (x - 50 * MenuScale, y + 4*MenuScale, GetLocalString("Options", "low"))					
	Text (x + width + 38 * MenuScale, y+4*MenuScale, GetLocalString("Options", "high"))	
	
	Return value
	
End Function


Function RowText(A$, X, Y, W, H, align% = 0, Leading#=1)
	;Display A$ starting at X,Y - no wider than W And no taller than H (all in pixels).
	;Leading is optional extra vertical spacing in pixels
	
	If H<1 Then H=2048
	
	Local LinesShown = 0
	Local Height = StringHeight(A$) + Leading
	Local b$
	
	While Len(A) > 0
		Local space = Instr(A$, " ")
		If space = 0 Then space = Len(A$)
		Local temp$ = Left(A$, space)
		Local trimmed$ = Trim(temp) ;we might ignore a final space 
		Local extra = 0 ;we haven't ignored it yet
		;ignore final space If doing so would make a word fit at End of Line:
		If (StringWidth (b$ + temp$) > W) And (StringWidth (b$ + trimmed$) <= W) Then
			temp = trimmed
			extra = 1
		EndIf
		
		If StringWidth (b$ + temp$) > W Then ;too big, so Print what will fit
			If align Then
				Text(X + W / 2 - (StringWidth(b) / 2), LinesShown * Height + Y, b)
			Else
				Text(X, LinesShown * Height + Y, b)
			EndIf			
			
			LinesShown = LinesShown + 1
			b$=""
		Else ;append it To b$ (which will eventually be printed) And remove it from A$
			b$ = b$ + temp$
			A$ = Right(A$, Len(A$) - (Len(temp$) + extra))
		EndIf
		
		If ((LinesShown + 1) * Height) > H Then Exit ;the Next Line would be too tall, so leave
	Wend
	
	If (b$ <> "") And((LinesShown + 1) <= H) Then
		If align Then
			Text(X + W / 2 - (StringWidth(b) / 2), LinesShown * Height + Y, b) ;Print any remaining Text If it'll fit vertically
		Else
			Text(X, LinesShown * Height + Y, b) ;Print any remaining Text If it'll fit vertically
		EndIf
	EndIf
	
End Function

Function GetLineAmount(A$, W, H, Leading#=1)
	;Display A$ starting at X,Y - no wider than W And no taller than H (all in pixels).
	;Leading is optional extra vertical spacing in pixels
	
	If H<1 Then H=2048
	
	Local LinesShown = 0
	Local Height = StringHeight(A$) + Leading
	Local b$
	
	While Len(A) > 0
		Local space = Instr(A$, " ")
		If space = 0 Then space = Len(A$)
		Local temp$ = Left(A$, space)
		Local trimmed$ = Trim(temp) ;we might ignore a final space 
		Local extra = 0 ;we haven't ignored it yet
		;ignore final space If doing so would make a word fit at End of Line:
		If (StringWidth (b$ + temp$) > W) And (StringWidth (b$ + trimmed$) <= W) Then
			temp = trimmed
			extra = 1
		EndIf
		
		If StringWidth (b$ + temp$) > W Then ;too big, so Print what will fit
			
			LinesShown = LinesShown + 1
			b$=""
		Else ;append it To b$ (which will eventually be printed) And remove it from A$
			b$ = b$ + temp$
			A$ = Right(A$, Len(A$) - (Len(temp$) + extra))
		EndIf
		
		If ((LinesShown + 1) * Height) > H Then Exit ;the Next Line would be too tall, so leave
	Wend
	
	Return LinesShown+1
	
End Function

Function DrawOptionsTooltip(x%,y%,width%,height%,option$,value#=0,ingame%=False)

	option = Lower(option)
	Local fx# = x+6*MenuScale
	Local fy# = y+6*MenuScale
	Local fw# = width-12*MenuScale
	Local fh# = height-12*MenuScale
	Local lines% = 0, lines2% = 0
	Local txt$ = GetLocalString("Options", option + "txt")
	Local txt2$ = GetLocalString("Options", option + "txt2")
	Local R% = 0, G% = 0, B% = 0
	
	SetFont I_Opt\Fonts[1]
	Color 255,255,255
	Select option
		;Graphic options
		Case "bump"
			R = 255
			txt2 = GetLocalString("Options", "cantingame")
		Case "antialias"
			R = 255
		Case "gamma"
			R = 255
			G = 255
			B = 255
			txt2 = Replace(txt2, "%s", Int(value*100))
		Case "particleamount"
			Select value
				Case 0
					R = 255
					txt2 = GetLocalString("Options", "particleamounttxt2a")
				Case 1
					R = 255
					G = 255
					txt2 = GetLocalString("Options", "particleamounttxt2b")
				Case 2
					G = 255
					txt2 = GetLocalString("Options", "particleamounttxt2c")
			End Select
		Case "vram"
			R = 255
			txt2 = GetLocalString("Options", "cantingame")
		Case "fov"
			R = 255
			G = 255
			B = 255
			txt2 = Replace(txt2, "%s", Int(FOV))
		;Sound options
		Case "musicvol"
			R = 255
			G = 255
			B = 255
			txt2 = Replace(txt2, "%s", Int(value*100))
		Case "soundvol"
			R = 255
			G = 255
			B = 255
			txt2 = Replace(txt2, "%s", Int(value*100))
		Case "sfxautorelease"
			R = 255
			txt2 = GetLocalString("Options", "cantingame")
		Case "usertrack"
			R = 255
			txt2 = GetLocalString("Options", "cantingame")
		Case "usertrackmode"
			R = 255
			G = 255
		;Control options	
		Case "mousesensitivity"
			R = 255
			G = 255
			B = 255
			txt2 = Replace(txt2, "%s", Int((0.5+value)*100))
		Case "mousesmoothing"
			R = 255
			G = 255
			B = 255
			txt2 = Replace(txt2, "%s", Int(value*100))
		;Advanced options	
		Case "consoleenable"
			txt = Replace(txt, "%s", I_Keys\KeyName[I_Keys\CONSOLE])
		Case "framelimit"
			If value > 0 And value < 60
				R = 255
				G = 255
			Else
				txt2 = ""
			EndIf
	End Select
	
	lines% = GetLineAmount(txt,fw,fh)
	If txt2$ = ""
		DrawFrame(x,y,width,(StringHeight(txt)*lines)+(10+lines)*MenuScale)
	Else
		lines2% = GetLineAmount(txt2,fw,fh)
		DrawFrame(x,y,width,((StringHeight(txt)*lines)+(10+lines)*MenuScale)+(StringHeight(txt2)*lines2)+(10+lines2)*MenuScale)
	EndIf
	RowText(txt,fx,fy,fw,fh)
	If txt2$ <> ""
		Color R,G,B
		RowText(txt2,fx,(fy+(StringHeight(txt)*lines)+(5+lines)*MenuScale),fw,fh)
	EndIf
	
End Function

Global OnSliderID% = 0

Function Slider3(x%,y%,width%,value%,ID%,val1$,val2$,val3$)
	
	If MouseDown1 Then
		If (ScaledMouseX() >= x) And (ScaledMouseX() <= x+width+14) And (ScaledMouseY() >= y-8) And (ScaledMouseY() <= y+10)
			OnSliderID = ID
		EndIf
	EndIf
	
	Color 200,200,200
	Rect(x,y,width+14,10,True)
	Rect(x,y-8,4,14,True)
	Rect(x+(width/2)+5,y-8,4,14,True)
	Rect(x+width+10,y-8,4,14,True)
	
	If ID = OnSliderID
		If (ScaledMouseX() <= x+8)
			value = 0
		ElseIf (ScaledMouseX() >= x+width/2) And (ScaledMouseX() <= x+(width/2)+8)
			value = 1
		ElseIf (ScaledMouseX() >= x+width)
			value = 2
		EndIf
		Color 0,255,0
		Rect(x,y,width+14,10,True)
	Else
		If (ScaledMouseX() >= x) And (ScaledMouseX() <= x+width+14) And (ScaledMouseY() >= y-8) And (ScaledMouseY() <= y+10)
			Color 0,200,0
			Rect(x,y,width+14,10,False)
		EndIf
	EndIf
	
	If value = 0
		DrawImage(BlinkMeterIMG,x,y-8)
	ElseIf value = 1
		DrawImage(BlinkMeterIMG,x+(width/2)+3,y-8)
	Else
		DrawImage(BlinkMeterIMG,x+width+6,y-8)
	EndIf
	
	Color 170,170,170
	If value = 0
		Text(x+2,y+10+MenuScale,val1,True)
	ElseIf value = 1
		Text(x+(width/2)+7,y+10+MenuScale,val2,True)
	Else
		Text(x+width+12,y+10+MenuScale,val3,True)
	EndIf
	
	Return value
	
End Function

Function Slider5(x%,y%,width%,value%,ID%,val1$,val2$,val3$,val4$,val5$)
	
	If MouseDown1 Then
		If (ScaledMouseX() >= x) And (ScaledMouseX() <= x+width+14) And (ScaledMouseY() >= y-8) And (ScaledMouseY() <= y+10)
			OnSliderID = ID
		EndIf
	EndIf
	
	Color 200,200,200
	Rect(x,y,width+14,10,True)
	Rect(x,y-8,4,14,True) ;1
	Rect(x+(width/4)+2.5,y-8,4,14,True) ;2
	Rect(x+(width/2)+5,y-8,4,14,True) ;3
	Rect(x+(width*0.75)+7.5,y-8,4,14,True) ;4
	Rect(x+width+10,y-8,4,14,True) ;5
	
	If ID = OnSliderID
		If (ScaledMouseX() <= x+8)
			value = 0
		ElseIf (ScaledMouseX() >= x+width/4) And (ScaledMouseX() <= x+(width/4)+8)
			value = 1
		ElseIf (ScaledMouseX() >= x+width/2) And (ScaledMouseX() <= x+(width/2)+8)
			value = 2
		ElseIf (ScaledMouseX() >= x+width*0.75) And (ScaledMouseX() <= x+(width*0.75)+8)
			value = 3
		ElseIf (ScaledMouseX() >= x+width)
			value = 4
		EndIf
		Color 0,255,0
		Rect(x,y,width+14,10,True)
	Else
		If (ScaledMouseX() >= x) And (ScaledMouseX() <= x+width+14) And (ScaledMouseY() >= y-8) And (ScaledMouseY() <= y+10)
			Color 0,200,0
			Rect(x,y,width+14,10,False)
		EndIf
	EndIf
	
	If value = 0
		DrawImage(BlinkMeterIMG,x,y-8)
	ElseIf value = 1
		DrawImage(BlinkMeterIMG,x+(width/4)+1.5,y-8)
	ElseIf value = 2
		DrawImage(BlinkMeterIMG,x+(width/2)+3,y-8)
	ElseIf value = 3
		DrawImage(BlinkMeterIMG,x+(width*0.75)+4.5,y-8)
	Else
		DrawImage(BlinkMeterIMG,x+width+6,y-8)
	EndIf
	
	Color 170,170,170
	If value = 0
		Text(x+2,y+10+MenuScale,val1,True)
	ElseIf value = 1
		Text(x+(width/4)+4.5,y+10+MenuScale,val2,True)
	ElseIf value = 2
		Text(x+(width/2)+7,y+10+MenuScale,val3,True)
	ElseIf value = 3
		Text(x+(width*0.75)+9.5,y+10+MenuScale,val4,True)
	Else
		Text(x+width+12,y+10+MenuScale,val5,True)
	EndIf
	
	Return value
	
End Function