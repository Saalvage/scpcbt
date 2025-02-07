Function UpdateConsole()
	
	Local e.Events
	
	If I_Opt\ConsoleEnabled = False Then
		ConsoleOpen = False
		Return
	EndIf
	
	If ConsoleOpen Then
		Local cm.ConsoleMsg
		
		SetFont I_Opt\Fonts[0]
		
		ConsoleR = 255 : ConsoleG = 255 : ConsoleB = 255
		
		Local x% = 0, y% = I_Opt\GraphicHeight-300*MenuScale, width% = I_Opt\GraphicWidth, height% = 300*MenuScale-30*MenuScale
		Local StrTemp$, temp%,  i%
		Local ev.Events, r.Rooms, it.Items
		
		DrawFrame x,y,width,height+30*MenuScale
		
		Local consoleHeight% = 0
		Local scrollbarHeight% = 0
		For cm.ConsoleMsg = Each ConsoleMsg
			consoleHeight = consoleHeight + 15*MenuScale
		Next
		scrollbarHeight = (Float(height)/Float(consoleHeight))*height
		If scrollbarHeight>height Then scrollbarHeight = height
		If consoleHeight<height Then consoleHeight = height
		
		Color 50,50,50
		inBar% = MouseOn(x+width-26*MenuScale,y,26*MenuScale,height)
		If inBar Then Color 70,70,70
		Rect x+width-26*MenuScale,y,26*MenuScale,height,True
		
		
		Color 120,120,120
		inBox% = MouseOn(x+width-23*MenuScale,y+height-scrollbarHeight+(ConsoleScroll*scrollbarHeight/height),20*MenuScale,scrollbarHeight)
		If inBox Then Color 200,200,200
		If ConsoleScrollDragging Then Color 255,255,255
		Rect x+width-23*MenuScale,y+height-scrollbarHeight+(ConsoleScroll*scrollbarHeight/height),20*MenuScale,scrollbarHeight,True
		
		If Not MouseDown(1) Then
			ConsoleScrollDragging=False
		ElseIf ConsoleScrollDragging Then
			ConsoleScroll = ConsoleScroll+((ScaledMouseY()-ConsoleMouseMem)*height/scrollbarHeight)
			ConsoleMouseMem = ScaledMouseY()
		EndIf
		
		If (Not ConsoleScrollDragging) Then
			If MouseHit1 Then
				If inBox Then
					ConsoleScrollDragging=True
					ConsoleMouseMem = ScaledMouseY()
				ElseIf inBar Then
					ConsoleScroll = ConsoleScroll+((ScaledMouseY()-(y+height))*consoleHeight/height+(height/2))
					ConsoleScroll = ConsoleScroll/2
				EndIf
			EndIf
		EndIf
		
		mouseScroll = MouseZSpeed()
		If mouseScroll=1 Then
			ConsoleScroll = ConsoleScroll - 15*MenuScale
		ElseIf mouseScroll=-1 Then
			ConsoleScroll = ConsoleScroll + 15*MenuScale
		EndIf
		
		Local reissuePos%
		If KeyHit(200) Then
			reissuePos% = 0
			If (ConsoleReissue=Null) Then
				ConsoleReissue=First ConsoleMsg
				
				While (ConsoleReissue<>Null)
					If (ConsoleReissue\isCommand) Then
						Exit
					EndIf
					reissuePos = reissuePos - 15*MenuScale
					ConsoleReissue = After ConsoleReissue
				Wend
				
			Else
				cm.ConsoleMsg = First ConsoleMsg
				While cm<>Null
					If cm=ConsoleReissue Then Exit
					reissuePos = reissuePos-15*MenuScale
					cm = After cm
				Wend
				ConsoleReissue = After ConsoleReissue
				reissuePos = reissuePos-15*MenuScale
				
				While True
					If (ConsoleReissue=Null) Then
						ConsoleReissue=First ConsoleMsg
						reissuePos = 0
					EndIf
				
					If (ConsoleReissue\isCommand) Then
						Exit
					EndIf
					reissuePos = reissuePos - 15*MenuScale
					ConsoleReissue = After ConsoleReissue
				Wend
			EndIf
			
			If ConsoleReissue<>Null Then
				ConsoleInput = ConsoleReissue\txt
				ConsoleScroll = reissuePos+(height/2)
				CursorPos = Len(ConsoleInput)
			EndIf
		EndIf
		
		If KeyHit(208) Then
			reissuePos% = -consoleHeight+15*MenuScale
			If (ConsoleReissue=Null) Then
				ConsoleReissue=Last ConsoleMsg
				
				While (ConsoleReissue<>Null)
					If (ConsoleReissue\isCommand) Then
						Exit
					EndIf
					reissuePos = reissuePos + 15*MenuScale
					ConsoleReissue = Before ConsoleReissue
				Wend
				
			Else
				cm.ConsoleMsg = Last ConsoleMsg
				While cm<>Null
					If cm=ConsoleReissue Then Exit
					reissuePos = reissuePos+15*MenuScale
					cm = Before cm
				Wend
				ConsoleReissue = Before ConsoleReissue
				reissuePos = reissuePos+15*MenuScale
				
				While True
					If (ConsoleReissue=Null) Then
						ConsoleReissue=Last ConsoleMsg
						reissuePos=-consoleHeight+15*MenuScale
					EndIf
				
					If (ConsoleReissue\isCommand) Then
						Exit
					EndIf
					reissuePos = reissuePos + 15*MenuScale
					ConsoleReissue = Before ConsoleReissue
				Wend
			EndIf
			
			If ConsoleReissue<>Null Then
				ConsoleInput = ConsoleReissue\txt
				ConsoleScroll = reissuePos+(height/2)
				CursorPos = Len(ConsoleInput)
			EndIf
		EndIf
		
		If ConsoleScroll<-consoleHeight+height Then ConsoleScroll = -consoleHeight+height
		If ConsoleScroll>0 Then ConsoleScroll = 0
		
		Color 255, 255, 255
		
		SelectedInputBox = 2
		Local oldConsoleInput$ = ConsoleInput
		ConsoleInput = InputBox(x, y + height, width, 30*MenuScale, ConsoleInput, 2, 100)
		If oldConsoleInput<>ConsoleInput Then
			ConsoleReissue = Null
		EndIf
		
		If KeyHit(28) And ConsoleInput <> "" Then
			ConsoleReissue = Null
			ConsoleScroll = 0
			CreateConsoleMsg(ConsoleInput,255,255,0,True)
			If Instr(ConsoleInput, " ") > 0 Then
				StrTemp$ = Lower(Left(ConsoleInput, Instr(ConsoleInput, " ") - 1))
			Else
				StrTemp$ = Lower(ConsoleInput)
			EndIf
			
			Select Lower(StrTemp)
				Case "help"

					If Instr(ConsoleInput, " ")<>0 Then
						StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Else
						StrTemp$ = ""
					EndIf
					ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 255
					
					Select Lower(StrTemp)
						Case "1", ""
							;[Block]
							CreateConsoleMsg("LIST OF COMMANDS - PAGE 1/3")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("- asd")
							CreateConsoleMsg("- status")
							CreateConsoleMsg("- fov")
							CreateConsoleMsg("- ending")
							CreateConsoleMsg("- noclipspeed")
							CreateConsoleMsg("- noclip")
							CreateConsoleMsg("- noblink")
							CreateConsoleMsg("- notarget")
							CreateConsoleMsg("- infinitestamina")
							CreateConsoleMsg("- heal")
							CreateConsoleMsg("- wireframe")
							CreateConsoleMsg("- 173speed")
							CreateConsoleMsg("- 106speed")
							CreateConsoleMsg("- 173state")
							CreateConsoleMsg("- 106state")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Use " + Chr(34) + "help 2/3" + Chr(34) + " to find more commands.")
							CreateConsoleMsg("Use " + Chr(34) + "help [command name]" + Chr(34) + " to get more information about a command.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "2"
							;[Block]
							CreateConsoleMsg("LIST OF COMMANDS - PAGE 2/3")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("- reset096")
							CreateConsoleMsg("- disable173")
							CreateConsoleMsg("- enable173")
							CreateConsoleMsg("- disable106")
							CreateConsoleMsg("- enable106")
							CreateConsoleMsg("- 106retreat")
							CreateConsoleMsg("- halloween")
							CreateConsoleMsg("- sanic")
							CreateConsoleMsg("- scp-420-j")
							CreateConsoleMsg("- godmode")
							CreateConsoleMsg("- revive")
							CreateConsoleMsg("- showfps")
							CreateConsoleMsg("- 096state")
							CreateConsoleMsg("- debughud")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Use " + Chr(34) + "help 3/3" + Chr(34) + " to find more commands.")
							CreateConsoleMsg("Use " + Chr(34) + "help [command name]" + Chr(34) + " to get more information about a command.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "3"
							;[Block]
							CreateConsoleMsg("- playmusic [clip + .wav/.ogg]")
							CreateConsoleMsg("- camerafog [near] [far]")
							CreateConsoleMsg("- gamma [value]")
							CreateConsoleMsg("- notarget")
							CreateConsoleMsg("- unlockexits")
							CreateConsoleMsg("- spawn [npc type] [state]")
							CreateConsoleMsg("- teleport [room name]")
							CreateConsoleMsg("- spawnitem [item name]")
							CreateConsoleMsg("- injure [value]")
							CreateConsoleMsg("- infect [value]")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Use " + Chr(34) + "help [command name]" + Chr(34) + " to get more information about a command.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Case "asd"
							CreateConsoleMsg("HELP - asd")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Actives all cheats")
							CreateConsoleMsg("******************************")
						Case "camerafog"
							CreateConsoleMsg("HELP - camerafog")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Sets the draw distance of the fog.")
							CreateConsoleMsg("The fog begins generating at 'CameraFogNear' units")
							CreateConsoleMsg("away from the camera and becomes completely opaque")
							CreateConsoleMsg("at 'CameraFogFar' units away from the camera.")
							CreateConsoleMsg("Example: camerafog 20 40")
							CreateConsoleMsg("******************************")
						Case "gamma"
							CreateConsoleMsg("HELP - gamma")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Sets the gamma correction.")
							CreateConsoleMsg("Should be set to a value between 0.0 and 2.0.")
							CreateConsoleMsg("Default is 1.0.")
							CreateConsoleMsg("******************************")
						Case "noclip", "fly"
							CreateConsoleMsg("HELP - noclip")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Toggles noclip, unless a valid parameter")
							CreateConsoleMsg("is specified (on/off).")
							CreateConsoleMsg("Allows the camera to move in any direction while")
							CreateConsoleMsg("bypassing collision.")
							CreateConsoleMsg("******************************")
						Case "godmode", "god"
							CreateConsoleMsg("HELP - godmode")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Toggles godmode, unless a valid parameter")
							CreateConsoleMsg("is specified (on/off).")
							CreateConsoleMsg("Prevents player death under normal circumstances.")
							CreateConsoleMsg("******************************")
						Case "wireframe"
							CreateConsoleMsg("HELP - wireframe")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Toggles wireframe, unless a valid parameter")
							CreateConsoleMsg("is specified (on/off).")
							CreateConsoleMsg("Allows only the edges of geometry to be rendered,")
							CreateConsoleMsg("making everything else transparent.")
							CreateConsoleMsg("******************************")
						Case "spawnitem"
							CreateConsoleMsg("HELP - spawnitem")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Spawns an item at the player's location.")
							CreateConsoleMsg("Any name that can appear in your inventory")
							CreateConsoleMsg("is a valid parameter.")
							CreateConsoleMsg("Example: spawnitem Key Card Omni")
							CreateConsoleMsg("******************************")
						Case "spawn"
							CreateConsoleMsg("HELP - spawn")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Spawns an NPC at the player's location.")
							CreateConsoleMsg("Valid parameters are:")
							CreateConsoleMsg("008zombie / 049 / 049-2 / 066 / 096 / 106 / 173")
							CreateConsoleMsg("/ 178-1 / 372 / 513-1 / 966 / 1499-1 / class-d")
							CreateConsoleMsg("/ guard / mtf / apache / tentacle / vehicle")
							CreateConsoleMsg("******************************")
						Case "revive","undead","resurrect"
							CreateConsoleMsg("HELP - revive")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Resets the player's death timer after the dying")
							CreateConsoleMsg("animation triggers.")
							CreateConsoleMsg("Does not affect injury, blood loss")
							CreateConsoleMsg("or 008 infection values.")
							CreateConsoleMsg("******************************")
						Case "teleport"
							CreateConsoleMsg("HELP - teleport")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Teleports the player to the first instance")
							CreateConsoleMsg("of the specified room. Any room that appears")
							CreateConsoleMsg("in rooms.ini is a valid parameter.")
							CreateConsoleMsg("******************************")
						Case "stopsound", "stfu"
							CreateConsoleMsg("HELP - stopsound")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Stops all currently playing sounds.")
							CreateConsoleMsg("******************************")
						Case "fov"
							CreateConsoleMsg("HELP - fov")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Field of view (FOV) is the amount of game view")
							CreateConsoleMsg("that is on display during a game.")
							CreateConsoleMsg("******************************")	
						Case "status"
							CreateConsoleMsg("HELP - status")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Prints player, camera, and room information.")
							CreateConsoleMsg("******************************")
						Case "weed","scp-420-j","420"
							CreateConsoleMsg("HELP - 420")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Generates dank memes.")
							CreateConsoleMsg("******************************")
						Case "playmusic"
							CreateConsoleMsg("HELP - playmusic")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Will play tracks in .ogg/.wav format")
							CreateConsoleMsg("from "+Chr(34)+"SFX\Music\Custom\"+Chr(34)+".")
							CreateConsoleMsg("******************************")
						Case "noblink", "nb"
							;[Block]
							CreateConsoleMsg("HELP - noblink")
							CreateConsoleMsg("******************************")
							CreateConsoleMsg("Toggles NoBlink, unless a valid parameter")
							CreateConsoleMsg("is specified (on/off).")
							CreateConsoleMsg("Prevents the player from having to blink.")
							CreateConsoleMsg("******************************")
							;[End Block]
						Default
							CreateConsoleMsg("There is no help available for that command.",255,150,0)
					End Select
					
				Case "status"

					ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 0
					CreateConsoleMsg("******************************")
					CreateConsoleMsg("Status: ")
					CreateConsoleMsg("Coordinates: ")
					CreateConsoleMsg("	- collider: "+EntityX(Collider)+", "+EntityY(Collider)+", "+EntityZ(Collider))
					CreateConsoleMsg("	- camera: "+EntityX(Camera)+", "+EntityY(Camera)+", "+EntityZ(Camera))
					
					CreateConsoleMsg("Rotation: ")
					CreateConsoleMsg("	- collider: "+EntityPitch(Collider)+", "+EntityYaw(Collider)+", "+EntityRoll(Collider))
					CreateConsoleMsg("	- camera: "+EntityPitch(Camera)+", "+EntityYaw(Camera)+", "+EntityRoll(Camera))
					
					CreateConsoleMsg("Room: "+PlayerRoom\RoomTemplate\Name)
					For ev.Events = Each Events
						If ev\room = PlayerRoom Then
							CreateConsoleMsg("Room event: "+ev\EventName)	
							CreateConsoleMsg("-	state: "+ev\EventState)
							CreateConsoleMsg("-	state2: "+ev\EventState2)	
							CreateConsoleMsg("-	state3: "+ev\EventState3)
							Exit
						EndIf
					Next
					
					CreateConsoleMsg("Room coordinates: "+Floor(EntityX(PlayerRoom\obj) / 8.0 + 0.5)+", "+ Floor(EntityZ(PlayerRoom\obj) / 8.0 + 0.5))
					CreateConsoleMsg("Stamina: "+Stamina)
					CreateConsoleMsg("Death timer: "+KillTimer)					
					CreateConsoleMsg("Blinktimer: "+BlinkTimer)
					CreateConsoleMsg("Injuries: "+Injuries)
					CreateConsoleMsg("Bloodloss: "+Bloodloss)
					CreateConsoleMsg("******************************")
				Case "fov"
					FOV = Int(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
				Case "hidedistance"
					HideDistance = Float(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					CreateConsoleMsg("Hidedistance set to " + HideDistance)
				Case "ending"
					SelectedEnding = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					KillTimer = -0.1
					;EndingTimer = -0.1
				Case "noclipspeed"
					I_Cheats\NoClipSpeed = Float(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
				Case "injure"
					Injuries = Float(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
				Case "infect"
					Infect = Float(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
				Case "heal"
					;[Block]
					Injuries = 0.0
					Bloodloss = 0.0
					
					BlurTimer = 0.0
					
					Infect = 0.0
					
					DeafTimer = 0.0
					DeathTimer = 0.0
					
					Stamina = 100.0
					
					For i = 0 To 5
						SCP1025State[i] = 0.0
					Next
					
					If I_427\Timer >= 70.0 * 360.0 Then I_427\Timer = 0.0
					
					For e.Events = Each Events
						If e\EventName = "1048a" Then 
							If PlayerRoom = e\room Then BlinkTimer = -10.0
							If e\room\Objects[0] <> 0 Then
								FreeEntity(e\room\Objects[0]) : e\room\Objects[0] = 0
							EndIf
							RemoveEvent(e)
						EndIf
					Next
					
					If BlinkEffect > 1.0 Then 
						BlinkEffect = 1.0
						BlinkEffectTimer = 0.0
					EndIf
					
					If StaminaEffect > 1.0 Then
						StaminaEffect = 1.0
						StaminaEffectTimer = 0.0
					EndIf
					;[End Block]
				Case "teleport"
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					For r.Rooms = Each Rooms
						If r\RoomTemplate\Name = StrTemp Then
							;PositionEntity (Collider, EntityX(r\obj), 0.7, EntityZ(r\obj))
							PositionEntity (Collider, EntityX(r\obj), EntityY(r\obj)+0.7, EntityZ(r\obj))
							ResetEntity(Collider)
							UpdateDoors()
							UpdateRooms()
							For it.Items = Each Items
								it\disttimer = 0
							Next
							PlayerRoom = r
							Exit
						EndIf
					Next
					
					If PlayerRoom\RoomTemplate\Name <> StrTemp Then CreateConsoleMsg("Room not found.",255,150,0)
				Case "spawnitem"
					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					temp = False 
					For itt.Itemtemplates = Each ItemTemplates
						If (Lower(itt\namespec) = StrTemp) Then
							temp = True
							CreateConsoleMsg(itt\localname + " spawned.")
							it.Items = CreateItem(itt\tempname, EntityX(Collider), EntityY(Camera,True), EntityZ(Collider), itt\namespec)
							EntityType(it\collider, HIT_ITEM)
							Exit
						ElseIf (Lower(itt\tempname) = StrTemp) Then
							temp = True
							CreateConsoleMsg(itt\localname + " spawned.")
							it.Items = CreateItem(itt\tempname, EntityX(Collider), EntityY(Camera,True), EntityZ(Collider), itt\namespec)
							EntityType(it\collider, HIT_ITEM)
							Exit
						ElseIf (Lower(itt\localname) = StrTemp) Then
							temp = True
							CreateConsoleMsg(itt\localname + " spawned.")
							it.Items = CreateItem(itt\tempname, EntityX(Collider), EntityY(Camera,True), EntityZ(Collider), itt\namespec)
							EntityType(it\collider, HIT_ITEM)
							Exit
						EndIf
					Next
					
					If temp = False Then CreateConsoleMsg("Item not found.",255,150,0)
				Case "wireframe"
					I_Cheats\WireframeState = Not I_Cheats\WireframeState
					
					If I_Cheats\WireframeState Then
						CreateConsoleMsg("WIREFRAME ON")
					Else
						CreateConsoleMsg("WIREFRAME OFF")	
					EndIf
					
					WireFrame I_Cheats\WireframeState
				Case "173speed"
					Curr173\Speed = Float(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					CreateConsoleMsg("173's speed set to " + Curr173\Speed)
				Case "106speed"
					Curr106\Speed = Float(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					CreateConsoleMsg("106's speed set to " + Curr106\Speed)
				Case "173state"
					CreateConsoleMsg("SCP-173")
					CreateConsoleMsg("Position: " + EntityX(Curr173\obj) + ", " + EntityY(Curr173\obj) + ", " + EntityZ(Curr173\obj))
					CreateConsoleMsg("Idle: " + Curr173\Idle)
					CreateConsoleMsg("State: " + Curr173\State)
					CreateConsoleMsg("Enabled: " + Enabled106)
				Case "106state"
					CreateConsoleMsg("SCP-106")
					CreateConsoleMsg("Position: " + EntityX(Curr106\obj) + ", " + EntityY(Curr106\obj) + ", " + EntityZ(Curr106\obj))
					CreateConsoleMsg("Idle: " + Curr106\Idle)
					CreateConsoleMsg("State: " + Curr106\State)
					CreateConsoleMsg("Enabled: " + Enabled106)
				Case "reset096"
					For n.NPCs = Each NPCs
						If n\NPCtype = NPCtype096 Then
							n\State = 0
							StopStream_Strict(n\SoundChn) : n\SoundChn=0
							If n\SoundChn2<>0
								StopStream_Strict(n\SoundChn2) : n\SoundChn2=0
							EndIf
							Exit
						EndIf
					Next
				Case "disable173"
					I_Cheats\Enabled173 = False
				Case "enable173"
					I_Cheats\Enabled173 = True
				Case "disable106"
					I_Cheats\Enabled106 = False
				Case "enable106"
					I_Cheats\Enabled106 = True
				Case "halloween"
					I_Opt\HalloweenTex = Not I_Opt\HalloweenTex
					If I_Opt\HalloweenTex Then
						Local tex = LoadTexture_Strict("GFX\npcs\173h.pt", 1)
						EntityTexture Curr173\obj, tex, 0, 0
						FreeTexture tex
						CreateConsoleMsg("173 JACK-O-LANTERN ON")
					Else
						Local tex2 = LoadTexture_Strict("GFX\npcs\173texture.jpg", 1)
						EntityTexture Curr173\obj, tex2, 0, 0
						FreeTexture tex2
						CreateConsoleMsg("173 JACK-O-LANTERN OFF")
					EndIf
				Case "speed", "sanic"
					I_Cheats\Speed = Not I_Cheats\Speed
					
					If I_Cheats\Speed Then
						CreateConsoleMsg("GOTTA GO FAST")
					Else
						CreateConsoleMsg("WHOA SLOW DOWN")
					EndIf
				Case "scp-420-j","420","weed"
					For i = 1 To 20
						If Rand(2)=1 Then
							it.Items = CreateItem("scp420j", EntityX(Collider,True)+Cos((360.0/20.0)*i)*Rnd(0.3,0.5), EntityY(Camera,True), EntityZ(Collider,True)+Sin((360.0/20.0)*i)*Rnd(0.3,0.5))
						Else
							it.Items = CreateItem("420s", EntityX(Collider,True)+Cos((360.0/20.0)*i)*Rnd(0.3,0.5), EntityY(Camera,True), EntityZ(Collider,True)+Sin((360.0/20.0)*i)*Rnd(0.3,0.5), "joint")
						EndIf
						EntityType (it\collider, HIT_ITEM)
					Next
					PlaySound_Strict LoadTempSound("SFX\Music\420J.ogg")
				Case "godmode", "god"
					I_Cheats\GodMode = Not I_Cheats\GodMode
					
					If I_Cheats\GodMode Then
						CreateConsoleMsg("GODMODE ON")
					Else
						CreateConsoleMsg("GODMODE OFF")
					EndIf
				Case "noblink", "nb"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							;[Block]
							I_Cheats\NoBlink = True		
							;[End Block]
						Case "off", "0", "false"
							;[Block]
							I_Cheats\NoBlink = False
							;[End Block]
						Default
							;[Block]
							I_Cheats\NoBlink = (Not I_Cheats\NoBlink)
							;[End Block]
					End Select	
					If I_Cheats\NoBlink Then
						CreateConsoleMsg("NOBLINK ON")
					Else
						CreateConsoleMsg("NOBLINK OFF")	
					EndIf
					;[End Block]
				Case "revive", "undead", "resurrect"
					DropSpeed = -0.1
					HeadDropSpeed = 0.0
					Shake = 0
					CurrSpeed = 0
					
					HeartBeatVolume = 0
					
					CameraShake = 0
					Shake = 0
					LightFlash = 0
					BlurTimer = 0
					
					For i = 0 To 7
						SCP1025state[i]=0
					Next
					
					DeathTimer = 0
					Infect = 0
					Injuries = 0
					Bloodloss = 0
					Stamina = 100
					If StaminaEffect > 1.0 Then
						StaminaEffect = 1.0
						StaminaEffectTimer = 0.0
					EndIf
					
					FallTimer = 0
					MenuOpen = False
					
					;If death by 173 and 106, enable GodMode to prevent instant death again ~ Salvage
					If DeathMSG = GetLocalString("Deaths", "173") Lor DeathMSG = GetLocalString("Deaths", "173intro") Lor DeathMSG = GetLocalString("Deaths", "173lock") Lor DeathMSG = GetLocalString("Deaths", "173doors") Then
						I_Cheats\GodMode = 1
						Curr173\Idle = False
						CreateConsoleMsg("Death by SCP-173 causes GodMode to be enabled!")
					ElseIf EntityDistanceSquared(Collider, Curr106\Collider) < PowTwo(1.0) Then
						I_Cheats\GodMode = 1
						CreateConsoleMsg("Death by SCP-106 causes GodMode to be enabled!")
					EndIf
					
					For np.NPCs = Each NPCs
						If np\NPCtype = NPCtype049 And np\state = 3
							np\state = 1
						EndIf
					Next
					
					ShowEntity Collider
					
					MoveEntity(Collider, 0, 2, 0)
					
					ResetEntity(Collider)
					
					KillTimer = 0
					KillAnim = 0
				Case "noclip", "fly"
					I_Cheats\NoClip = Not I_Cheats\NoClip
					
					If I_Cheats\NoClip Then
						Playable = True
						CreateConsoleMsg("NOCLIP ON")
					Else
						RotateEntity Collider, 0, EntityYaw(Collider), 0
						CreateConsoleMsg("NOCLIP OFF")
					EndIf
					
					DropSpeed = 0
				Case "showfps"
					I_Opt\ShowFPS = Not I_Opt\ShowFPS
					CreateConsoleMsg("ShowFPS: " + I_Opt\ShowFPS)
				Case "096state"
					For n.NPCs = Each NPCs
						If n\NPCtype = NPCtype096 Then
							CreateConsoleMsg("SCP-096")
							CreateConsoleMsg("Position: " + EntityX(n\obj) + ", " + EntityY(n\obj) + ", " + EntityZ(n\obj))
							CreateConsoleMsg("Idle: " + n\Idle)
							CreateConsoleMsg("State: " + n\State)
							Exit
						EndIf
					Next
					
					CreateConsoleMsg("SCP-096 has not spawned.")
				Case "debughud"
					DebugHUD = Not DebugHUD
					
					If DebugHUD Then
						CreateConsoleMsg("Debug HUD On")
					Else
						CreateConsoleMsg("Debug HUD Off")
					EndIf
				Case "stopsound", "stfu"
					For snd.Sound = Each Sound
						For i = 0 To 31
							If snd\channels[i]<>0 Then
								StopChannel snd\channels[i]
							EndIf
						Next
					Next
					
					If IntercomStreamCHN <> 0 Then
						StopStream_Strict(IntercomStreamCHN)
						IntercomStreamCHN = 0
					EndIf
					
					For e.Events = Each Events
						If e\EventName = "alarm" Then 
							If e\room\NPC[0] <> Null Then RemoveNPC(e\room\NPC[0])
							If e\room\NPC[1] <> Null Then RemoveNPC(e\room\NPC[1])
							If e\room\NPC[2] <> Null Then RemoveNPC(e\room\NPC[2])
							
							FreeEntity e\room\Objects[0] : e\room\Objects[0]=0
							FreeEntity e\room\Objects[1] : e\room\Objects[1]=0
							PositionEntity Curr173\Collider, 0,0,0
							ResetEntity Curr173\Collider
							ShowEntity Curr173\obj
							ShowEntity Curr173\obj2
							RemoveEvent(e)
							Exit
						EndIf
					Next
					
					CreateConsoleMsg("Stopped all sounds.")
				Case "camerafog"
					args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					CameraFogNear = Float(Left(args, Len(args) - Instr(args, " ")))
					CameraFogFar = Float(Right(args, Len(args) - Instr(args, " ")))
					CreateConsoleMsg("Near set to: " + CameraFogNear + ", far set to: " + CameraFogFar)
				Case "gamma"

					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					ScreenGamma = Int(StrTemp)
					CreateConsoleMsg("Gamma set to " + ScreenGamma)

				Case "spawn"

					args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					StrTemp$ = Piece$(args$, 1)
					StrTemp2$ = Piece$(args$, 2)
					
					;Hacky fix for when the user doesn't input a second parameter.
					If (StrTemp <> StrTemp2) Then
						Console_SpawnNPC(StrTemp, StrTemp2)
					Else
						Console_SpawnNPC(StrTemp)
					EndIf
				Case "infinitestamina", "infstam", "is"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							;[Block]
							I_Cheats\InfiniteStamina = True	
							;[End Block]
						Case "off", "0", "false"
							;[Block]
							I_Cheats\InfiniteStamina = False
							;[End Block]
						Default
							;[Block]
							I_Cheats\InfiniteStamina = (Not I_Cheats\InfiniteStamina)
							;[End Block]
					End Select
					
					If I_Cheats\InfiniteStamina
						CreateConsoleMsg("INFINITE STAMINA ON")
					Else
						CreateConsoleMsg("INFINITE STAMINA OFF")	
					EndIf
					;[End Block]
				Case "asd"
					;[Block]
					I_Cheats\GodMode = True
					I_Cheats\NoClip = True
					I_Cheats\InfiniteStamina = True
					I_Cheats\NoBlink = True
					I_Cheats\NoTarget = True
					CameraFogNear = INFINITY
					CameraFogFar = INFINITY
					
					; stfu command
					For snd.Sound = Each Sound
						For i = 0 To 31
							If snd\Channels[i] <> 0 Then
								StopChannel(snd\Channels[i])
							EndIf
						Next
					Next
					
					If IntercomStreamCHN <> 0 Then
						StopStream_Strict(IntercomStreamCHN)
						IntercomStreamCHN = 0
					EndIf
					
					For e.Events = Each Events
						If e\EventName = "alarm" Then 
							If e\room\NPC[0] <> Null Then RemoveNPC(e\room\NPC[0])
							If e\room\NPC[1] <> Null Then RemoveNPC(e\room\NPC[1])
							If e\room\NPC[2] <> Null Then RemoveNPC(e\room\NPC[2])
							
							FreeEntity(e\room\Objects[0]) : e\room\Objects[0] = 0
							FreeEntity(e\room\Objects[1]) : e\room\Objects[1] = 0
							PositionEntity(Curr173\Collider, 0.0, 0.0, 0.0)
							ResetEntity(Curr173\Collider)
							ShowEntity(Curr173\OBJ)
							ShowEntity(Curr173\OBJ2)
							RemoveEvent(e)
							Exit
						EndIf
					Next
					CreateConsoleMsg("Stopped all sounds.")
					;[End Block]
				Case "cls", "clear"
					For c.ConsoleMsg = Each ConsoleMsg
						Delete c
					Next
				Case "togglewarhead"
					;[Block]
					For e.Events = Each Events
						If e\EventName = "room2nuke" Then
							e\EventState = (Not e\EventState)
							UpdateLever(e\room\Objects[1])
							UpdateLever(e\room\Objects[3])
							RotateEntity(e\room\Objects[1], 0.0, EntityYaw(e\room\Objects[1]), e\EventState * 30.0)
							RotateEntity(e\room\Objects[3], 0.0, EntityYaw(e\room\Objects[3]), e\EventState * 30.0)
							;Exit
						EndIf
					Next
					;[End Block]
				Case "togglecontrol"
					;[Block]
					For e.Events = Each Events
						If e\EventName = "room2ccont" Then
							RemoteDoorOn = (Not RemoteDoorOn)
							UpdateLever(e\room\Objects[5])
							RotateEntity(e\room\Objects[5], 0.0, EntityYaw(e\room\Objects[5]), RemoteDoorOn * 30.0)
							;Exit
						EndIf
					Next
					;[End Block]
				Case "unlockexits"

					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "a"
							For e.Events = Each Events
								If e\EventName = "gateaentrance" Then
									e\EventState3 = 1
									e\room\RoomDoors[1]\open = True
									Exit
								EndIf
							Next
							CreateConsoleMsg("Gate A is now unlocked.")	
						Case "b"
							For e.Events = Each Events
								If e\EventName = "gateb" Then
									e\EventState3 = 1
									e\room\RoomDoors[4]\open = True
									Exit
								EndIf
							Next	
							CreateConsoleMsg("Gate B is now unlocked.")	
						Default
							For e.Events = Each Events
								If e\EventName = "gateaentrance" Then
									e\EventState3 = 1
									e\room\RoomDoors[1]\open = True
								ElseIf e\EventName = "gateb" Then
									e\EventState3 = 1
									e\room\RoomDoors[4]\open = True
								EndIf
							Next
							CreateConsoleMsg("Gate A and B are now unlocked.")	
					End Select
					
					RemoteDoorOn = True

				Case "kill","suicide"

					KillTimer = -1
					Select Rand(4)
						Case 1
							DeathMSG = GetLocalString("Deaths", "redacted")
						Case 2
							DeathMSG = GetLocalString("Deaths", "noreason")
						Case 3
							DeathMSG = GetLocalString("Deaths", "exception")
						Case 4
							DeathMSG = GetLocalString("Deaths", "kys")
					End Select

				Case "playmusic"

					; I think this might be broken since the FMod library streaming was added. -Mark
					If Instr(ConsoleInput, " ")<>0 Then
						StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Else
						StrTemp$ = ""
					EndIf
					
					If StrTemp$ <> ""
						PlayCustomMusic% = True
						If CustomMusic <> 0 Then FreeSound_Strict CustomMusic : CustomMusic = 0
						If MusicCHN <> 0 Then StopChannel MusicCHN
						CustomMusic = LoadSound_Strict("SFX\Music\Custom\"+StrTemp$)
						If CustomMusic = 0
							PlayCustomMusic% = False
						EndIf
					Else
						PlayCustomMusic% = False
						If CustomMusic <> 0 Then FreeSound_Strict CustomMusic : CustomMusic = 0
						If MusicCHN <> 0 Then StopChannel MusicCHN
					EndIf

				Case "tpmtf"

					For n.NPCs = Each NPCs
						If n\NPCtype = NPCtypeMTF
							If n\MTFLeader = Null
								PositionEntity Collider,EntityX(n\Collider),EntityY(n\Collider)+5,EntityZ(n\Collider)
								ResetEntity Collider
								Exit
							EndIf
						EndIf
					Next
				Case "tele"
					args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					StrTemp$ = Piece$(args$,1," ")
					StrTemp2$ = Piece$(args$,2," ")
					StrTemp3$ = Piece$(args$,3," ")
					PositionEntity Collider,Float(StrTemp$),Float(StrTemp2$),Float(StrTemp3$)
					PositionEntity Camera,Float(StrTemp$),Float(StrTemp2$),Float(StrTemp3$)
					ResetEntity Collider
					ResetEntity Camera
					CreateConsoleMsg("Teleported to coordinates (X|Y|Z): "+EntityX(Collider)+"|"+EntityY(Collider)+"|"+EntityZ(Collider))
				Case "notarget", "nt"
					;[Block]
					StrTemp = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					Select StrTemp
						Case "on", "1", "true"
							;[Block]
							I_Cheats\NoTarget = True	
							;[End Block]
						Case "off", "0", "false"
							;[Block]
							I_Cheats\NoTarget = False	
							;[End Block]
						Default
							;[Block]
							I_Cheats\NoTarget = (Not I_Cheats\NoTarget)
							;[End Block]
					End Select
					
					If I_Cheats\NoTarget = False Then
						CreateConsoleMsg("NOTARGET OFF")
					Else
						CreateConsoleMsg("NOTARGET ON")	
					EndIf
					;[End Block]
				Case "spawnulti", "ulti"
					it.Items = CreateItem("keyomni", EntityX(Collider), EntityY(Camera,True), EntityZ(Collider))
					EntityType(it\collider, HIT_ITEM)
				
					it.Items = CreateItem("navulti", EntityX(Collider), EntityY(Camera,True), EntityZ(Collider))
					EntityType(it\collider, HIT_ITEM)
					
					it.Items = CreateItem("fineradio", EntityX(Collider), EntityY(Camera,True), EntityZ(Collider))
					EntityType(it\collider, HIT_ITEM)
					it\state = 101
					
					it.Items = CreateItem("finenvg", EntityX(Collider), EntityY(Camera,True), EntityZ(Collider))
					EntityType(it\collider, HIT_ITEM)
					
					it.Items = CreateItem("scramble", EntityX(Collider), EntityY(Camera,True), EntityZ(Collider))
					EntityType(it\collider, HIT_ITEM)
					
					it.Items = CreateItem("supergasmask", EntityX(Collider), EntityY(Camera,True), EntityZ(Collider))
					EntityType(it\collider, HIT_ITEM)
				Case "spawnpumpkin","pumpkin"
					CreateConsoleMsg("What pumpkin?")
				Case "teleport173"
					If I_Cheats\Enabled173 Then
						PositionEntity Curr173\Collider,EntityX(Collider),EntityY(Collider)+0.2,EntityZ(Collider)
						ResetEntity Curr173\Collider
					Else
						CreateConsoleMsg("SCP-173 is currently disabled, re-enable it in order to use this command.")
					EndIf
				Case "seteventstate"
					args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					StrTemp$ = Piece$(args$,1," ")
					StrTemp2$ = Piece$(args$,2," ")
					StrTemp3$ = Piece$(args$,3," ")
					Local pl_room_found% = False
					If StrTemp="" Lor StrTemp2="" Lor StrTemp3=""
						CreateConsoleMsg("Too few parameters. This command requires 3.",255,150,0)
					Else
						For e.Events = Each Events
							If e\room = PlayerRoom
								If Lower(StrTemp)<>"keep"
									e\EventState = Float(StrTemp)
								EndIf
								If Lower(StrTemp2)<>"keep"
									e\EventState2 = Float(StrTemp2)
								EndIf
								If Lower(StrTemp3)<>"keep"
									e\EventState3 = Float(StrTemp3)
								EndIf
								CreateConsoleMsg("Changed event states from current player room to: "+e\EventState+"|"+e\EventState2+"|"+e\EventState3)
								pl_room_found = True
								Exit
							EndIf
						Next
						If (Not pl_room_found)
							CreateConsoleMsg("The current room doesn't has any event applied.",255,150,0)
						EndIf
					EndIf

				Case "spawnparticles"

					If Instr(ConsoleInput, " ")<>0 Then
						StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Else
						StrTemp$ = ""
					EndIf
					
					If Int(StrTemp) > -1 And Int(StrTemp) <= 1 ;<--- This is the maximum ID of particles by Devil Particle system, will be increased after time - ENDSHN
						SetEmitter(Collider,ParticleEffect[Int(StrTemp)])
						CreateConsoleMsg("Spawned particle emitter with ID "+Int(StrTemp)+" at player's position.")
					Else
						CreateConsoleMsg("Particle emitter with ID "+Int(StrTemp)+" not found.",255,150,0)
					EndIf

				Case "giveachievement"

					If Instr(ConsoleInput, " ")<>0 Then
						StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					Else
						StrTemp$ = ""
					EndIf
					
					If Int(StrTemp)>=0 And Int(StrTemp)<MAXACHIEVEMENTS
						Achievements[Int(StrTemp)]=True
						CreateConsoleMsg("Achievemt "+AchievementStrings[Int(StrTemp)]+" unlocked.")
					Else
						CreateConsoleMsg("Achievement with ID "+Int(StrTemp)+" doesn't exist.",255,150,0)
					EndIf

				Case "427state"

					StrTemp$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					
					I_427\Timer = Float(StrTemp)*70.0

				Case "teleport106"

					If I_Cheats\Enabled106 Then
						Curr106\State = 0
						Curr106\Idle = False
					Else
						CreateConsoleMsg("SCP-106 is currently disabled, re-enable it in order to use this command.")
					EndIf

				Case "106retreat"

					If Curr106\State <= 0 Then
						Curr106\State = Rand(22000, 27000)
						PositionEntity Curr106\Collider,0,500,0
					Else
						CreateConsoleMsg("SCP-106 is currently not active, so it cannot retreat.")
					EndIf

				Case "setblinkeffect"

					args$ = Lower(Right(ConsoleInput, Len(ConsoleInput) - Instr(ConsoleInput, " ")))
					BlinkEffect = Float(Left(args, Len(args) - Instr(args, " ")))
					BlinkEffectTimer = Float(Right(args, Len(args) - Instr(args, " ")))
					CreateConsoleMsg("Set BlinkEffect to: " + BlinkEffect + "and BlinkEffect timer: " + BlinkEffectTimer)

				Case "jorge"
	
					CreateConsoleMsg(Chr(74)+Chr(79)+Chr(82)+Chr(71)+Chr(69)+Chr(32)+Chr(72)+Chr(65)+Chr(83)+Chr(32)+Chr(66)+Chr(69)+Chr(69)+Chr(78)+Chr(32)+Chr(69)+Chr(88)+Chr(80)+Chr(69)+Chr(67)+Chr(84)+Chr(73)+Chr(78)+Chr(71)+Chr(32)+Chr(89)+Chr(79)+Chr(85)+Chr(46))
				
				Case "up"
					If CurrentZone > 0
						SaveGame(CurrSave\Name, -1)
						NullGame(False)
						If FileType(SavePath + CurrSave\Name + "\" + (CurrentZone - 1) + ".zone") = 1 Then
							LoadEntities()
							LoadAllSounds()
							LoadGame(CurrSave\Name)
							InitLoadGame()
						Else
							InitNewGame(CurrentZone - 1)
							LoadGameQuick(CurrSave\Name, False)
						EndIf
						SaveGame(CurrSave\Name)
					EndIf
				
				Case "down"
					If CurrentZone < 2
						SaveGame(CurrSave\Name, +1)
						NullGame(False)
						If FileType(SavePath + CurrSave\Name + "\" + (CurrentZone + 1) + ".zone") = 1 Then
							LoadEntities()
							LoadAllSounds()
							LoadGame(CurrSave\Name)
							InitLoadGame()
						Else
							InitNewGame(CurrentZone + 1)
							LoadGameQuick(CurrSave\Name, False)
						EndIf
						SaveGame(CurrSave\Name)
					EndIf
					
				Case "freeze"
					Freeze = Not Freeze
				
				Default
					CreateConsoleMsg("Command not found.",255,0,0)

			End Select
			
			ConsoleInput = ""
			CursorPos = 0
		EndIf
		
		Local TempY% = y + height - 25*MenuScale - ConsoleScroll
		Local count% = 0
		For cm.ConsoleMsg = Each ConsoleMsg
			count = count+1
			If count>1000 Then
				Delete cm
			Else
				If TempY >= y And TempY < y + height - 20*MenuScale Then
					If cm=ConsoleReissue Then
						Color cm\r/4,cm\g/4,cm\b/4
						Rect x,TempY-2*MenuScale,width-30*MenuScale,24*MenuScale,True
					EndIf
					Color cm\r,cm\g,cm\b
					If cm\isCommand Then
						Text(x + 20*MenuScale, TempY, "> "+cm\txt)
					Else
						Text(x + 20*MenuScale, TempY, cm\txt)
					EndIf
				EndIf
				TempY = TempY - 15*MenuScale
			EndIf
			
		Next
		
		Color 255,255,255
		
		If I_Opt\GraphicMode = 0 Then DrawImage CursorIMG, ScaledMouseX(),ScaledMouseY()
	EndIf
	
	SetFont I_Opt\Fonts[1]
	
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D