Local InitErrorStr$ = ""
If FileSize("fmod.dll") = 0 Then InitErrorStr = InitErrorStr + "fmod.dll" + Chr(13) + Chr(10)
If FileSize("d3dim700.dll") = 0 Then InitErrorStr = InitErrorStr + "d3dim700.dll" + Chr(13) + Chr(10)
If FileSize("BlitzMovie.dll") = 0 Then InitErrorStr = InitErrorStr + "BlitzMovie.dll" + Chr(13) + Chr(10)
If FileSize("FreeImage.dll") = 0 Then InitErrorStr = InitErrorStr + "FreeImage.dll" + Chr(13) + Chr(10)

If Len(InitErrorStr) > 0 Then
	RuntimeError "The following DLLs were not found in the game directory:" + Chr(13) + Chr(10) + Chr(13) + Chr(10) + InitErrorStr
EndIf

Include "Source Code\Math.bb"
Include "Source Code\DevilParticleSystem.bb"

Const OptionFile$ = "Data\options.ini"

If FileType(OptionFile) <> 1 Then
	DefaultOptionsINI()
EndIf

Type Options
	Field LauncherEnabled%
	Field GraphicWidth%
	Field GraphicHeight%
	Field GraphicMode%
	Field ShowFPS%
	Field ConsoleEnabled%
	Field ConsoleOnError%
	Field DebugMode%
	
	Field HalloweenTex%
	
	Field Fonts%[8]
End Type

Global I_Opt.Options = New Options

I_Opt\LauncherEnabled = GetINIInt(OptionFile, "options", "launcher enabled")
I_Opt\GraphicWidth = GetINIInt(OptionFile, "options", "width")
I_Opt\GraphicHeight = GetINIInt(OptionFile, "options", "height")
I_Opt\GraphicMode = GetINIInt(OptionFile, "options", "mode")
I_Opt\ShowFPS = GetINIInt(OptionFile, "options", "show FPS")
I_Opt\ConsoleEnabled = GetINIInt(OptionFile, "console", "enabled")
I_Opt\ConsoleOnError = GetINIInt(OptionFile, "console", "auto opening")
I_Opt\DebugMode = GetINIInt(OptionFile, "options", "debug mode")

Type Loc
	Field Lang$
	Field LangPath$
	Field Localized%
End Type

Type LocalString
	Field section$
	Field parameter$
	Field value$
End Type

Function UpdateLang(Lang$)
	If I_Loc\LangPath <> "" Then ;Only need to delete local and fonts, because this line is only ever called twice in the launcher
		DeleteINIFile(I_Loc\LangPath + "Data\local.ini")
		DeleteINIFile(I_Loc\LangPath + "GFX\font\fonts.ini")
	EndIf
	If Lang = "English" Then
		I_Loc\Lang = ""
		I_Loc\LangPath = ""
		I_Loc\Localized = False
	Else
		I_Loc\Lang = Lang
		I_Loc\LangPath = "Localization\" + Lang + "\"
		I_Loc\Localized = True
	EndIf
	For l.LocalString = Each LocalString
		Delete l
	Next
	;These are the strings to be cached in order to allow for better framerates.
	;Order is important, first created is fastest to access.
	SetLocalString("Messages", "savecantloc")
	SetLocalString("Messages", "savecantmom")
	SetLocalString("Messages", "saved")
	SetLocalString("Messages", "savepress")
	SetLocalString("Game", "nvgrefresh")
	SetLocalString("Game", "nvgseconds")
	SetLocalString("Game", "lowbat")
	SetLocalString("Game", "nobat")
	SetLocalString("Menu", "paused")
	SetLocalString("Menu", "difficulty")
	SetLocalString("Menu", "save")
	SetLocalString("Menu", "seed")
	SetLocalString("Menu", "resume")
	SetLocalString("Menu", "loadgame")
	SetLocalString("Menu", "ach")
	SetLocalString("Menu", "options")
	SetLocalString("Menu", "back")
	SetLocalString("Menu", "quit")
	ReloadFonts()
End Function

Global I_Loc.Loc = New Loc

UpdateLang(GetINIString(OptionFile, "options", "pack"))

Function SetLocalString(Section$, Parameter$)
	Local l.LocalString = New LocalString
	l\value = GetLocalString(Section, Parameter) ;need to set the value first, otherwise it is being set to itself
	l\section = Section
	l\parameter = Parameter
End Function

;Returns localized version of a String, if no translation exists, use English
Function GetLocalString$(Section$, Parameter$)
	
	For l.LocalString = Each LocalString
		If l\section = Section And l\parameter = Parameter Then
			Return l\value
		EndIf
	Next
	; TODO Find out all occassions where this is called every frame
	;CreateConsoleMsg("Called " + Section + Parameter)
	
	Local temp$
	
	If I_Loc\Localized And FileType(I_Loc\LangPath + "Data\local.ini") = 1 Then
		temp=GetINIString(I_Loc\LangPath + "Data\local.ini", Section, Parameter)
		If temp <> "" Then
			Return temp
		EndIf
	EndIf
	
	temp=GetINIString("Data\local.ini", Section, Parameter)
	If temp <> "" Then
		Return temp
	EndIf
	
	Return Section + "." + Parameter
	
End Function

;With Formatting! %s in a String gets replaced
Function GetLocalStringR$(Section$, Parameter$, Replace$)
	
	Return Replace(GetLocalString(Section, Parameter), "%s", Replace)
	
End Function

Function LoadLocalFont(Font$, IgnoreScaling%=0)
	
	path$ = I_Loc\LangPath + "GFX\font\"
	file$ = path + "fonts.ini"
	
	If (Not I_Loc\Localized) Lor FileType(file) = 0 Then
		path = "GFX\font\"
		file = path + "fonts.ini"
	EndIf
	name$ = GetINIString(file, Font, "name")
	
	If name = "" Then
		path = "GFX\font\"
		file = path + "fonts.ini"
		name = GetINIString(file, Font, "name")
	EndIf
	
	name = path + name + ".ttf"
	
	If FileType(name) <> 1 Then RuntimeError("Font not found: " + Font + " : " + name)
	
	; Font size is handled via a budget ternary operator
	Local temp% = LoadFont(name, (Int(GetINIInt(file, Font, "size") * (I_Opt\GraphicHeight / 1024.0))) * (Not IgnoreScaling) + IgnoreScaling * GetINIInt(file, Font, "size"))
	If temp = 0 Then RuntimeError("Failed to load Font: " + Font + " : " + name)
	Return temp
	
End Function

Function ReloadFonts()
	;0: Console
	;1 - 5: 1 - 5
	;6 + 7: Credit 1 + 2
	I_Opt\Fonts[0] = LoadLocalFont("Font0")
	I_Opt\Fonts[1] = LoadLocalFont("Font1")
	I_Opt\Fonts[2] = LoadLocalFont("Font2")
	I_Opt\Fonts[3] = LoadLocalFont("Font3")
	I_Opt\Fonts[4] = LoadLocalFont("Font4")
	I_Opt\Fonts[5] = LoadLocalFont("Font5")
	
	SetFont I_Opt\Fonts[1]
End Function

Include "Source Code\StrictLoads.bb"
Include "Source Code\Keys.bb"

Const ErrorDir$ = "Logs\"
Global ErrorFile$ = ErrorDir + "error_log_"
Local ErrorFileInd% = 0
While FileType(ErrorFile+Str(ErrorFileInd)+".txt")<>0
	ErrorFileInd = ErrorFileInd+1
Wend
ErrorFile = ErrorFile+Str(ErrorFileInd)+".txt"

Const VersionNumber$ = "1.0.0"
Const CompatibleNumber$ = "1.0.0" ;Lowest version with compatible saves

Global MenuWhite%, MenuBlack%
Global ButtonSFX%, ButtonSFX2%

Global EnableSFXRelease% = GetINIInt(OptionFile, "audio", "sfx release")
Global EnableSFXRelease_Prev% = EnableSFXRelease%

Global ArrowIMG[4]

Global Data294$ = "Data\SCP-294.ini"

Global AchvIni$ = "Data\achievements.ini"

Global fresize_image%, fresize_texture%, fresize_texture2%
Global fresize_cam%

Global Freeze%

Global RealGraphicWidth%,RealGraphicHeight%
Global AspectRatioRatio#

Global EnableRoomLights% = GetINIInt(OptionFile, "options", "room lights enabled")

Global TextureDetails% = GetINIInt(OptionFile, "options", "texture details")
Global TextureFloat#
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
Global SFXVolume# = GetINIFloat(OptionFile, "audio", "sound volume")

Global QuickLoad_CurrEvent.Events

Type LauncherOptions
	Field TotalGFXModes%
	Field GFXModes%
	Field SelectedGFXMode%
	Field GfxModeWidths%[64], GfxModeHeights%[64]
	Field TotalLocs%
	Field SelectedLoc%
	Field Locs$[2048]
End Type

ButtonSFX% = LoadSound_Strict("SFX\Interact\Button.ogg")
ButtonSFX2% = LoadSound_Strict("SFX\Interact\Button2.ogg")

If I_Opt\LauncherEnabled Then
	Local I_LOpt.LauncherOptions = New LauncherOptions

	I_LOpt\TotalGFXModes = CountGfxModes3D()
	I_LOpt\TotalLocs = GetFileAmount("Localization\", True)

	AspectRatioRatio = 1.0
	
	AppTitle GetLocalString("Menu", "titlelauncher")
	
	UpdateLauncher(I_LOpt)
	
	Delete I_LOpt
EndIf


;New "fake fullscreen" - ENDSHN Psst, it's called borderless windowed mode --Love Mark, But it's an alliteration (and it's true)!! ~Salvage
If I_Opt\GraphicMode = 1
	DebugLog "Using Borderless Windowed Mode"
	Graphics3DExt DesktopWidth(), DesktopHeight(), 4
	
	RealGraphicWidth = DesktopWidth()
	RealGraphicHeight = DesktopHeight()
	
	AspectRatioRatio = (Float(I_Opt\GraphicWidth)/Float(I_Opt\GraphicHeight))/(Float(RealGraphicWidth)/Float(RealGraphicHeight))
Else
	AspectRatioRatio = 1.0
	RealGraphicWidth = I_Opt\GraphicWidth
	RealGraphicHeight = I_Opt\GraphicHeight
	Graphics3DExt(I_Opt\GraphicWidth, I_Opt\GraphicHeight, (I_Opt\GraphicMode = 2) + 1)
EndIf

If FileType(I_Loc\LangPath + Data294) = 1 Then
	Data294 = I_Loc\LangPath + Data294
EndIf

If FileType(I_Loc\LangPath + AchvIni) = 1 Then
	AchvIni = I_Loc\LangPath + AchvIni
EndIf

Type Cheats 
	Field GodMode%
	Field NoClip%, NoClipSpeed#
	Field Enabled173%, Enabled106%
	Field SuperMan%, SuperManTimer#
	Field Speed%, InfiniteStamina%
	Field WireframeState%
	Field NoTarget%
	Field NoBlink%
End Type

Function ClearCheats(I_Cheats.Cheats)
	I_Cheats\GodMode = 0
	I_Cheats\NoClip = 0
	I_Cheats\NoClipSpeed = 2.0
	I_Cheats\Enabled173 = 1
	I_Cheats\Enabled106 = 1
	I_Cheats\SuperMan = 0
	I_Cheats\SuperManTimer = 0.0
	I_Cheats\InfiniteStamina = 0
	I_Cheats\Speed = 0.0
	I_Cheats\WireframeState = 0
	I_Cheats\NoTarget = 0
	I_Cheats\NoBlink = 0
End Function

Global I_Cheats.Cheats = New Cheats
ClearCheats(I_Cheats)

Global MenuScale# = (I_Opt\GraphicHeight / 1024.0)

SetBuffer(BackBuffer())

; FPSfactor 1 value: 70 FPS
Global CurTime%, PrevTime%, LoopDelay%, FPSfactor#, FPSfactor2#, PrevFPSFactor#
Local CheckFPS%, ElapsedLoops%, FPS%, ElapsedTime#

Global Framelimit% = GetINIInt(OptionFile, "options", "framelimit")
Global Vsync% = GetINIInt(OptionFile, "options", "vsync")

Global Opt_AntiAlias = GetINIInt(OptionFile, "options", "antialias")

Global ScreenGamma# = GetINIFloat(OptionFile, "options", "screengamma")

Global FOV% = GetINIInt(OptionFile, "options", "fov")

Const HIT_MAP% = 1, HIT_PLAYER% = 2, HIT_ITEM% = 3, HIT_APACHE% = 4, HIT_178% = 5, HIT_DEAD% = 6
SeedRnd MilliSecs()

Global GameSaved%

Global CanSave% = True

AppTitle GetLocalString("Menu", "title") + " v" + VersionNumber

If GetINIInt(OptionFile, "options", "play startup video")
	PlayStartupVideos()
EndIf

;---------------------------------------------------------------------------------------------------------------------

ReloadFonts()
SetFont I_Opt\Fonts[2]

Global CursorIMG% = LoadImage_Strict("GFX\cursor.png")

Global SelectedLoadingScreen.LoadingScreens, LoadingScreenAmount%, LoadingScreenText%
Global LoadingBack% = LoadImage_Strict("Loadingscreens\loadingback.jpg")
Global LoadingScreenScale# = Max(I_Opt\GraphicWidth/1920.0, I_Opt\GraphicHeight/1080.0)
If LoadingScreenScale <> 1.0 Then ScaleImage(LoadingBack, LoadingScreenScale, LoadingScreenScale)
InitLoadingScreens("Loadingscreens\loadingscreens.ini")

Global BlinkMeterIMG% = LoadImage_Strict("GFX\blinkmeter.jpg")

DrawLoading(0, True)

; - -Viewport.
Global viewport_center_x% = I_Opt\GraphicWidth / 2, viewport_center_y% = I_Opt\GraphicHeight / 2

; -- Mouselook.
Global mouselook_x_inc# = 0.3 ; This sets both the sensitivity and direction (+/-) of the mouse on the X axis.
Global mouselook_y_inc# = 0.3 ; This sets both the sensitivity and direction (+/-) of the mouse on the Y axis.
; Used to limit the mouse movement to within a certain number of pixels (250 is used here) from the center of the screen. This produces smoother mouse movement than continuously moving the mouse back to the center each loop.
Global mouse_left_limit% = 250, mouse_right_limit% = GraphicsWidth () - 250
Global mouse_top_limit% = 150, mouse_bottom_limit% = GraphicsHeight () - 150 ; As above.
Global mouse_x_speed_1#, mouse_y_speed_1#

Global MouseSmooth# = GetINIFloat(OptionFile,"options", "mouse smoothing", 1.0)

Global Mesh_MinX#, Mesh_MinY#, Mesh_MinZ#
Global Mesh_MaxX#, Mesh_MaxY#, Mesh_MaxZ#
Global Mesh_MagX#, Mesh_MagY#, Mesh_MagZ#

;player stats -------------------------------------------------------------------------------------------------------
Global KillTimer#, KillAnim%, FallTimer#, DeathTimer#
Global Sanity#, ForceMove#, ForceAngle#
Global RestoreSanity%

Global Playable% = True
Global CanBlinkDespitePlayable% = False

Global BLINKFREQ#
Global BlinkTimer#, EyeIrritation#, EyeStuck#, BlinkEffect# = 1.0, BlinkEffectTimer#

Global Stamina#, StaminaEffect#=1.0, StaminaEffectTimer#

Global CameraShakeTimer#, Vomit%, VomitTimer#, Regurgitate%

Global SCP1025state#[8]

Global HeartBeatRate#, HeartBeatTimer#, HeartBeatVolume#

Global WearingGasMask%, WearingHazmat%, WearingVest%, Wearing178%, Wearing714%, WearingNightVision%, WearingScramble%
Global NVTimer#, ScrambleActive%, ScrambleCHN%

Global Injuries#, Bloodloss#, Infect#, HealTimer#

Include "Source Code\Achievements.bb"

Include "Source Code\Console.bb"
Include "Source Code\Use914.bb"

Global RefinedItems%

;player coordinates, angle, speed, movement etc ---------------------------------------------------------------------
Global DropSpeed#, HeadDropSpeed#, CurrSpeed#
Global user_camera_pitch#, side#
Global Crouch%, CrouchState#
Global CrouchSFX%

Global PlayerZone%, PlayerRoom.Rooms, CurrentZone%

Global GrabbedEntity%

Global InvertMouse% = GetINIInt(OptionFile, "options", "invert mouse y")
Global InvertCam%
Global MouseHit1%, MouseDown1%, MouseHit2%, DoubleClick%, LastMouseHit1%, MouseUp1%, DoubleClickSlot%

Global CoffinDistance# = 100.0

Global PlayerSoundVolume#

;camera/lighting effects (blur, camera shake, etc)-------------------------------------------------------------------
Global Shake#

Global ExplosionTimer#, ExplosionSFX%

Global LightsOn% = True

Global SoundTransmission%

;menus, GUI ---------------------------------------------------------------------------------------------------------
Global MainMenuOpen%, MenuOpen%, StopHidingTimer#, InvOpen%
Global OtherOpen.Items = Null

Global SelectedEnding$, EndingScreen%, EndingTimer#

Global MsgTimer#, Msg$, DeathMSG$

Global AccessCode%, KeypadInput$, KeypadTimer#, KeypadMSG$

Global DrawHandIcon%
Global DrawArrowIcon%[4]

;misc ---------------------------------------------------------------------------------------------------------------

Include "Source Code\Difficulty.bb"

Global MTFtimer#, MTFrooms.Rooms[7], MTFroomState%[7]

Global RadioState#[9]
Global RadioState3%[7]
Global RadioState4%[10]
Global RadioCHN%[7]

Global OldAiPics%[7]

; Handlded in DrawGUI
Global PlayTime%
Global PlayingSince% ; = 0 means Paused

Function SavePlayTime()
	PlayTime = PlayTime + (MilliSecs() - PlayingSince)
	PlayingSince = 0
End Function

Global ConsoleFlush%
Global ConsoleFlushSnd% = 0, ConsoleMusFlush% = 0, ConsoleMusPlay% = 0

Global IsNVGBlinking% = False


;----------------------------------------------  Console -----------------------------------------------------

Global ConsoleOpen%, ConsoleInput$
Global ConsoleScroll#,ConsoleScrollDragging%
Global ConsoleMouseMem%
Global ConsoleReissue.ConsoleMsg = Null
Global ConsoleR% = 255,ConsoleG% = 255,ConsoleB% = 255

Type ConsoleMsg
	Field txt$
	Field isCommand%
	Field r%,g%,b%
End Type

Function CreateConsoleMsg(txt$,r%=-1,g%=-1,b%=-1,isCommand%=False)
	Local c.ConsoleMsg = New ConsoleMsg
	Insert c Before First ConsoleMsg
	
	c\txt = txt
	c\isCommand = isCommand
	
	c\r = r
	c\g = g
	c\b = b
	
	If (c\r<0) Then c\r = ConsoleR
	If (c\g<0) Then c\g = ConsoleG
	If (c\b<0) Then c\b = ConsoleB
End Function

ConsoleR = 0 : ConsoleG = 255 : ConsoleB = 255
CreateConsoleMsg("Console commands: ")
CreateConsoleMsg("  - teleport [room name]")
CreateConsoleMsg("  - godmode [on/off]")
CreateConsoleMsg("  - noclip [on/off]")
CreateConsoleMsg("  - noclipspeed [x] (default = 2.0)")
CreateConsoleMsg("  - wireframe [on/off]")
CreateConsoleMsg("  - debughud [on/off]")
CreateConsoleMsg("  - camerafog [near] [far]")
CreateConsoleMsg(" ")
CreateConsoleMsg("  - status")
CreateConsoleMsg("  - heal")
CreateConsoleMsg(" ")
CreateConsoleMsg("  - spawnitem [item name]")
CreateConsoleMsg(" ")
CreateConsoleMsg("  - 173speed [x] (default = 35)")
CreateConsoleMsg("  - disable173/enable173")
CreateConsoleMsg("  - disable106/enable106")
CreateConsoleMsg("  - 106retreat")
CreateConsoleMsg("  - 173state/106state/096state")
CreateConsoleMsg("  - spawn [npc type]")
CreateConsoleMsg("  - fov [x] (default = 65)")

;---------------------------------------------------------------------------------------------------

Global DebugHUD%

Global BlurVolume#, BlurTimer#

Global LightBlink#, LightFlash#

Global BumpEnabled% = GetINIInt(OptionFile, "options", "bump mapping enabled")
Global HUDenabled% = GetINIInt(OptionFile, "options", "HUD enabled")

Global Camera%, CameraShake#, CurrCameraZoom#

Global Brightness% = GetINIFloat(OptionFile, "options", "brightness")
Global CameraFogNear# = 0.5
Global CameraFogFar# = 6

Global StoredCameraFogFar# = CameraFogFar

Global MouseSens# = GetINIFloat(OptionFile, "options", "mouse sensitivity")

Global EnableVRam% = GetINIInt(OptionFile, "options", "enable vram")

Include "Source Code\Dreamfilter.bb"

;----------------------------------------------  Sounds -----------------------------------------------------

Global SoundEmitter%
Global TempSounds%[10]
Global TempSoundCHN%
Global TempSoundIndex% = 0

;The Music now has to be pre-defined, as the new system uses streaming instead of the usual sound loading system Blitz3D has
Global Music$[27]
Music[0] = "The Dread"
Music[1] = "HeavyContainment"
Music[2] = "EntranceZone"
Music[3] = "PD"
Music[4] = "079"
Music[5] = "GateB1"
Music[6] = "GateB2"
Music[7] = "Room3Storage"
Music[8] = "Room049"
Music[9] = "8601"
Music[10] = "106"
Music[11] = "Menu"
Music[12] = "8601Cancer"
Music[13] = "Intro"
Music[14] = "178"
Music[15] = "PDTrench"
Music[16] = "205"
Music[17] = "GateA"
Music[18] = "1499"
Music[19] = "1499Danger"
Music[20] = "049Chase"
Music[21] = "..\Ending\MenuBreath"
Music[22] = "914"
Music[23] = "Ending"
Music[24] = "Credits"
Music[25] = "SaveMeFrom"
Music[26] = "Room106"

Global MusicVolume# = GetINIFloat(OptionFile, "audio", "music volume")
;Global MusicCHN% = StreamSound_Strict("SFX\Music\"+Music[2]+".ogg", MusicVolume, CurrMusicStream)

Global CurrMusicStream, MusicCHN
MusicCHN = StreamSound_Strict("SFX\Music\"+Music[2]+".ogg",MusicVolume,2)

Global CurrMusicVolume# = 1.0, NowPlaying%=2, ShouldPlay%=11
Global CurrMusic% = 1

DrawLoading(10, True)

Dim OpenDoorSFX%(3,3), CloseDoorSFX%(3,3)
Dim BigDoorErrorSFX%(3)

Global KeyCardSFX1 
Global KeyCardSFX2
Global ScannerSFX1
Global ScannerSFX2 

Global OpenDoorFastSFX
Global CautionSFX% 

Global NuclearSirenSFX%

Global CameraSFX  

Global StoneDragSFX% 

Global GunshotSFX% 
Global Gunshot2SFX% 
Global Gunshot3SFX% 
Global BullethitSFX% 

Global TeslaIdleSFX 
Global TeslaActivateSFX 
Global TeslaPowerUpSFX 

Global MagnetUpSFX%, MagnetDownSFX
Global FemurBreakerSFX%
Global EndBreathCHN%
Global EndBreathSFX%

Dim DecaySFX%(5)

Global BurstSFX 

DrawLoading(20, True)

Dim RustleSFX%(3)

Global Use914SFX%
Global Death914SFX% 

Dim DripSFX%(4)

Global LeverSFX%, LightSFX% 
Global ButtGhostSFX% 

Dim RadioSFX(5,10) 

Global RadioSquelch%
Global RadioStatic%
Global RadioBuzz%

Global ElevatorBeepSFX, ElevatorMoveSFX  

Dim PickSFX%(10)

Global AmbientSFXCHN%, CurrAmbientSFX%
Dim AmbientSFXAmount(6)
;0 = light containment, 1 = heavy containment, 2 = entrance
AmbientSFXAmount(0)=8 : AmbientSFXAmount(1)=11 : AmbientSFXAmount(2)=12
;3 = general, 4 = pre-breach
AmbientSFXAmount(3)=15 : AmbientSFXAmount(4)=5
;5 = forest
AmbientSFXAmount(5)=10

Dim AmbientSFX%(6, 15)

Dim OldManSFX%(8)

Dim Scp173SFX%(3)

Dim HorrorSFX%(20)


DrawLoading(25, True)

Dim IntroSFX%(20)

;IntroSFX(13) = LoadSound_Strict("SFX\intro\shoot1.ogg")
;IntroSFX(14) = LoadSound_Strict("SFX\intro\shoot2.ogg")


Dim AlarmSFX%(5)

Dim CommotionState%(25)

Global HeartBeatSFX 

Global VomitSFX%

Dim BreathSFX(2,5)
Global BreathRelaxedSFX%
Global BreathRelaxedPlaying% = False
Global BreathCHN%


Dim NeckSnapSFX(3)

Dim DamageSFX%(9)

Dim MTFSFX%(8)

Dim CoughSFX%(3)
Global CoughCHN%, VomitCHN%

Global MachineSFX% 
Global ApacheSFX%
Global CurrStepSFX%
Dim StepSFX%(5, 2, 8) ;(normal/metal, walk/run, id)

Dim Step2SFX(6)

Dim VehicleSFX%(1)

Global ScrambleSFX%

DrawLoading(30, True)


;New Sounds and Meshes/Other things in SCP:CB 1.3 - ENDSHN

;Global NTF_1499EnterSFX% = LoadSound_Strict("SFX\SCP\1499\Enter.ogg")
;Global NTF_1499LeaveSFX% = LoadSound_Strict("SFX\SCP\1499\Exit.ogg")

Global PlayCustomMusic% = False, CustomMusic% = 0

Global Monitor2, Monitor3, MonitorTexture2, MonitorTexture3, MonitorTexture4, MonitorTextureOff
Global MonitorTimer# = 0.0, MonitorTimer2# = 0.0, UpdateCheckpoint1%, UpdateCheckpoint2%

;This variable is for when a camera detected the player
	;False: Player is not seen (will be set after every call of the Main Loop
	;True: The Player got detected by a camera
Global PlayerDetected%
Global PrevInjuries#,PrevBloodloss#

Global NVGImages = LoadImage("GFX\battery.png")
MaskImage NVGImages,255,0,255

Global Wearing1499% = False
Global AmbientLightRoomTex%, AmbientLightRoomVal%

Global EnableUserTracks% = GetINIInt(OptionFile, "audio", "enable user tracks")
Global UserTrackMode% = GetINIInt(OptionFile, "audio", "user track setting")
Global UserTrackCheck% = 0, UserTrackCheck2% = 0
Global UserTrackMusicAmount% = 0, CurrUserTrack%, UserTrackFlag% = False
Dim UserTrackName$(256)

Global NTF_1499PrevX#
Global NTF_1499PrevY#
Global NTF_1499PrevZ#
Global NTF_1499PrevRoom.Rooms
Global NTF_1499X#
Global NTF_1499Y#
Global NTF_1499Z#
Global NTF_1499Sky%

Global OptionsMenu% = 0
Global QuitMSG% = 0

Global InFacility% = True

Global PrevMusicVolume# = MusicVolume#
Global PrevSFXVolume# = SFXVolume#
Global DeafPlayer% = False
Global DeafTimer# = 0.0

Global IsZombie% = False

Global room2gw_brokendoor% = False
Global room2gw_x# = 0.0
Global room2gw_z# = 0.0

Global ParticleAmount% = GetINIInt(OptionFile,"options","particle amount")

Global NavImages[5]
For i = 0 To 3
	NavImages[i] = LoadImage_Strict("GFX\navigator\roomborder"+i+".png")
	MaskImage NavImages[i],255,0,255
Next
NavImages[4] = LoadImage_Strict("GFX\navigator\batterymeter.png")

Global NavBG = CreateImage(I_Opt\GraphicWidth,I_Opt\GraphicHeight)

Global LightConeModel

Global ParticleEffect%[3]

Global DTextures%[11]

Global NPC049OBJ, NPC0492OBJ
Global ClerkOBJ

Global IntercomStreamCHN%

Global ForestNPC,ForestNPCTex,ForestNPCData#[3]



;-----------------------------------------  Images ----------------------------------------------------------

Global PauseMenuIMG%

Global SprintIcon%
Global BlinkIcon%
Global CrouchIcon%
Global WalkIcon%
Global HandIcon%
Global HandIcon2%

Global StaminaMeterIMG%

Global KeypadHUD

Global Panel294, Using294%, Input294$

DrawLoading(35, True)

;----------------------------------------------  Items  -----------------------------------------------------

Include "Source Code\Items.bb"

;--------------------------------------- Particles ------------------------------------------------------------

Include "Source Code\Particles.bb"

;-------------------------------------  Doors --------------------------------------------------------------

Global ClosestButton%, ClosestDoor.Doors
Global SelectedDoor.Doors, UpdateDoorsTimer#
Global ButtonTexture%, ButtontextureLocked%
Global DoorTempID%
Type Doors
	Field obj%, obj2%, frameobj%, buttons%[2]
	Field locked%, lockedupdated%, open%, angle%, openstate#, fastopen%
	Field dir%
	Field timer%, timerstate#
	Field KeyCard%
	Field room.Rooms
	
	Field DisableWaypoint%
	
	Field dist#
	
	Field SoundCHN%
	
	Field Code$
	
	Field ID%
	
	Field Level%
	Field LevelDest%
	
	Field AutoClose%
	
	Field LinkedDoor.Doors
	
	Field IsElevatorDoor% = False
	
	Field MTFClose% = True
	Field NPCCalledElevator% = False
	
	Field DoorHitOBJ%
End Type

Dim BigDoorOBJ(2), HeavyDoorObj(2)
Dim OBJTunnel(7)

Function CreateDoor.Doors(lvl, x#, y#, z#, angle#, room.Rooms, dopen% = False,  big% = False, keycard% = False, code$="", useCollisionMesh% = False)
	Local d.Doors, parent, i%
	If room <> Null Then parent = room\obj
	
	Local d2.Doors
	
	d.Doors = New Doors
	If big=1 Then
		d\obj = CopyEntity(BigDoorOBJ(0))
		ScaleEntity(d\obj, 55 * RoomScale, 55 * RoomScale, 55 * RoomScale)
		d\obj2 = CopyEntity(BigDoorOBJ(1))
		ScaleEntity(d\obj2, 55 * RoomScale, 55 * RoomScale, 55 * RoomScale)
		
		d\frameobj = CopyEntity(DoorColl)	;CopyMesh				
		ScaleEntity(d\frameobj, RoomScale, RoomScale, RoomScale)
		EntityType d\frameobj, HIT_MAP
		EntityAlpha d\frameobj, 0.0
	ElseIf big=2 Then
		d\obj = CopyEntity(HeavyDoorObj(0))
		ScaleEntity(d\obj, RoomScale, RoomScale, RoomScale)
		d\obj2 = CopyEntity(HeavyDoorObj(1))
		ScaleEntity(d\obj2, RoomScale, RoomScale, RoomScale)
		
		d\frameobj = CopyEntity(DoorFrameOBJ)
	ElseIf big=3 Then
		For d2 = Each Doors
			If d2 <> d And d2\dir = 3 Then
				d\obj = CopyEntity(d2\obj)
				d\obj2 = CopyEntity(d2\obj2)
				ScaleEntity d\obj, RoomScale, RoomScale, RoomScale
				ScaleEntity d\obj2, RoomScale, RoomScale, RoomScale
				Exit
			EndIf
		Next
		If d\obj=0 Then
			d\obj = LoadMesh_Strict("GFX\map\elevatordoor.b3d")
			d\obj2 = CopyEntity(d\obj)
			ScaleEntity d\obj, RoomScale, RoomScale, RoomScale
			ScaleEntity d\obj2, RoomScale, RoomScale, RoomScale
		EndIf
		d\frameobj = CopyEntity(DoorFrameOBJ)
	ElseIf big=5 Then
		For d2 = Each Doors
			If d2 <> d And d2\dir = 5 Then
				d\obj = CopyEntity(d2\obj)
				ScaleEntity d\obj, RoomScale, RoomScale, RoomScale
				Exit
			EndIf
		Next
		If d\obj=0 Then
			d\obj = LoadMesh_Strict("GFX\map\DoorWindowed.b3d")
			ScaleEntity d\obj, RoomScale, RoomScale, RoomScale
		EndIf
		d\frameobj = CopyEntity(DoorFrameOBJ)
	Else
		d\obj = CopyEntity(DoorOBJ)
		ScaleEntity(d\obj, (204.0 * RoomScale) / MeshWidth(d\obj), 312.0 * RoomScale / MeshHeight(d\obj), 16.0 * RoomScale / MeshDepth(d\obj))
		
		d\frameobj = CopyEntity(DoorFrameOBJ)
		d\obj2 = CopyEntity(DoorOBJ)
		
		ScaleEntity(d\obj2, (204.0 * RoomScale) / MeshWidth(d\obj), 312.0 * RoomScale / MeshHeight(d\obj), 16.0 * RoomScale / MeshDepth(d\obj))
		;entityType d\obj2, HIT_MAP
	EndIf
	
	;scaleentity(d\obj, 0.1, 0.1, 0.1)
	PositionEntity d\frameobj, x, y, z	
	ScaleEntity(d\frameobj, (8.0 / 2048.0), (8.0 / 2048.0), (8.0 / 2048.0))
	EntityPickMode d\frameobj,2
	EntityType d\obj, HIT_MAP
	If d\obj2 <> 0 Then EntityType d\obj2, HIT_MAP
	
	d\ID = DoorTempID
	DoorTempID=DoorTempID+1
	
	d\KeyCard = keycard
	d\Code = code
	
	d\Level = lvl
	d\LevelDest = 66
	
	For i% = 0 To 1
		If code <> "" Then 
			d\buttons[i]= CopyEntity(ButtonCodeOBJ)
			EntityFX(d\buttons[i], 1)
		Else
			If keycard>0 Then
				d\buttons[i]= CopyEntity(ButtonKeyOBJ)
			ElseIf keycard<0
				d\buttons[i]= CopyEntity(ButtonScannerOBJ)
			ElseIf big = 3
				If i = 0 Then
					d\buttons[i] = CopyEntity(ButtonElevatorOBJ)
				Else
					d\buttons[i] = CopyEntity(ButtonOBJ)
				EndIf
			Else
				d\buttons[i] = CopyEntity(ButtonOBJ)
			EndIf
		EndIf
		
		ScaleEntity(d\buttons[i], 0.03, 0.03, 0.03)
		
		If big = 1 Then
			If i = 0 Then
				PositionEntity(d\buttons[i], x - 432.0 * RoomScale, y + 0.7, z + 192.0 * RoomScale)
			Else
				PositionEntity(d\buttons[i], x + 432.0 * RoomScale, y + 0.7, z - 192.0 * RoomScale)
			EndIf
			RotateEntity(d\buttons[i], 0.0, 90.0 + (i * 180.0), 0.0)
		Else
			If i = 0 Then
				PositionEntity(d\buttons[i], x + 0.6, y + 0.7, z - 0.1)
			Else
				PositionEntity(d\buttons[i], x - 0.6, y + 0.7, z + 0.1)
				RotateEntity(d\buttons[i], 0.0, 180.0, 0.0)
			EndIf
		EndIf
		
		EntityParent(d\buttons[i], d\frameobj)
		EntityPickMode(d\buttons[i], 2)
	Next
	
	PositionEntity d\obj, x, y, z
	
	RotateEntity d\obj, 0, angle, 0
	RotateEntity d\frameobj, 0, angle, 0
	
	If d\obj2 <> 0 Then
		PositionEntity d\obj2, x, y, z
		If big=1 Then
			RotateEntity(d\obj2, 0, angle, 0)
		Else
			RotateEntity(d\obj2, 0, angle + 180, 0)
		EndIf
		EntityParent(d\obj2, parent)
	EndIf
	
	EntityParent(d\frameobj, parent)
	EntityParent(d\obj, parent)
	
	d\angle = angle
	d\open = dopen		
	
	EntityPickMode(d\obj, 2)
	If d\obj2 <> 0 Then
		EntityPickMode(d\obj2, 2)
	EndIf
	
	EntityPickMode d\frameobj,2
	
	If d\open And big = False And Rand(8) = 1 Then d\AutoClose = True
	If big = 5 Then d\dir = 0 Else d\dir = big
	d\room=room
	
	d\MTFClose = True
	
	If useCollisionMesh Then
		For d2.Doors = Each Doors
			If d2 <> d Then
				If d2\DoorHitOBJ <> 0 Then
					d\DoorHitOBJ = CopyEntity(d2\DoorHitOBJ,d\frameobj)
					EntityAlpha d\DoorHitOBJ,0.0
					EntityFX d\DoorHitOBJ,1
					EntityType d\DoorHitOBJ,HIT_MAP
					EntityColor d\DoorHitOBJ,255,0,0
					HideEntity d\DoorHitOBJ
					Exit
				EndIf
			EndIf
		Next
		If d\DoorHitOBJ=0 Then
			d\DoorHitOBJ = LoadMesh_Strict("GFX\doorhit.b3d",d\frameobj)
			EntityAlpha d\DoorHitOBJ,0.0
			EntityFX d\DoorHitOBJ,1
			EntityType d\DoorHitOBJ,HIT_MAP
			EntityColor d\DoorHitOBJ,255,0,0
			HideEntity d\DoorHitOBJ
		EndIf
	EndIf
	
	Return d
	
End Function

Function CreateButton(x#,y#,z#, pitch#,yaw#,roll#=0, Locked% = False)
	Local obj% = CopyEntity(ButtonOBJ)	
	
	ScaleEntity(obj, 0.03, 0.03, 0.03)
	If Locked Then EntityTexture(obj, ButtontextureLocked)
	PositionEntity obj, x,y,z
	RotateEntity obj, pitch,yaw,roll
	
	EntityPickMode(obj, 2)	
	
	Return obj
End Function

Function UpdateDoors()
	
	Local i%, d.Doors
	If UpdateDoorsTimer =< 0 Then
		For d.Doors = Each Doors
			
			d\dist = Abs(EntityX(Collider)-EntityX(d\obj,True)) + Abs(EntityZ(Collider)-EntityZ(d\obj,True))
			
			If d\dist > HideDistance*2 Then
				If d\obj <> 0 Then HideEntity d\obj
				If d\frameobj <> 0 Then HideEntity d\frameobj
				If d\obj2 <> 0 Then HideEntity d\obj2
				If d\buttons[0] <> 0 Then HideEntity d\buttons[0]
				If d\buttons[1] <> 0 Then HideEntity d\buttons[1]				
			Else
				If d\obj <> 0 Then ShowEntity d\obj
				If d\frameobj <> 0 Then ShowEntity d\frameobj
				If d\obj2 <> 0 Then ShowEntity d\obj2
				If d\buttons[0] <> 0 Then ShowEntity d\buttons[0]
				If d\buttons[1] <> 0 Then ShowEntity d\buttons[1]
			EndIf
		Next
		
		UpdateDoorsTimer = 30
	Else
		UpdateDoorsTimer = Max(UpdateDoorsTimer-FPSfactor,0)
	EndIf
	
	If (Not Freeze) Then
		ClosestButton = 0
		ClosestDoor = Null
		
		For d.Doors = Each Doors
			If d\dist < HideDistance*2 Lor d\IsElevatorDoor>0 Then ;Make elevator doors update everytime because if not, this can cause a bug where the elevators suddenly won't work, most noticeable in room2mtunnels - ENDSHN
				
				If (d\openstate >= 180 Lor d\openstate <= 0) And GrabbedEntity = 0 Then
					For i% = 0 To 1
						If d\buttons[i] <> 0 Then
							If Abs(EntityX(Collider)-EntityX(d\buttons[i],True)) < 1.0 Then 
								If Abs(EntityZ(Collider)-EntityZ(d\buttons[i],True)) < 1.0 Then 
									Local dist# = DistanceSquared(EntityX(Collider, True), EntityX(d\buttons[i], True), EntityZ(Collider, True), EntityZ(d\buttons[i], True));entityDistance(collider, d\buttons[i])
									If dist < PowTwo(0.7) Then
										Local temp% = CreatePivot()
										PositionEntity temp, EntityX(Camera), EntityY(Camera), EntityZ(Camera)
										PointEntity temp,d\buttons[i]
										
										If EntityPick(temp, 0.6) = d\buttons[i] Then
											If ClosestButton = 0 Then
												ClosestButton = d\buttons[i]
												ClosestDoor = d
											Else
												If dist < EntityDistanceSquared(Collider, ClosestButton) Then ClosestButton = d\buttons[i] : ClosestDoor = d
											EndIf							
										EndIf
										
										FreeEntity temp
									EndIf							
								EndIf
							EndIf
							
						EndIf
					Next
				EndIf
				
				If d\open Then
					If d\openstate < 180 Then
						Select d\dir
							Case 0, 5
								d\openstate = Min(180, d\openstate + FPSfactor * 2 * (d\fastopen+1))
								MoveEntity(d\obj, Sin(d\openstate) * (d\fastopen*2+1) * FPSfactor / 80.0, 0, 0)
								If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate)* (d\fastopen+1) * FPSfactor / 80.0, 0, 0)		
							Case 1
								d\openstate = Min(180, d\openstate + FPSfactor * 0.8)
								MoveEntity(d\obj, Sin(d\openstate) * FPSfactor / 180.0, 0, 0)
								If d\obj2 <> 0 Then MoveEntity(d\obj2, -Sin(d\openstate) * FPSfactor / 180.0, 0, 0)
							Case 2
								d\openstate = Min(180, d\openstate + FPSfactor * 2 * (d\fastopen+1))
								MoveEntity(d\obj, Sin(d\openstate) * (d\fastopen+1) * FPSfactor / 85.0, 0, 0)
								If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate)* (d\fastopen*2+1) * FPSfactor / 120.0, 0, 0)
							Case 3
								d\openstate = Min(180, d\openstate + FPSfactor * 2 * (d\fastopen+1))
								MoveEntity(d\obj, Sin(d\openstate) * (d\fastopen*2+1) * FPSfactor / 162.0, 0, 0)
								If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate)* (d\fastopen*2+1) * FPSfactor / 162.0, 0, 0)
							Case 4 ;Used for 914 only
								d\openstate = Min(180, d\openstate + FPSfactor * 1.4)
								MoveEntity(d\obj, Sin(d\openstate) * FPSfactor / 114.0, 0, 0)
						End Select
					Else
						d\fastopen = 0
						ResetEntity(d\obj)
						If d\obj2 <> 0 Then ResetEntity(d\obj2)
						If d\timerstate > 0 Then
							d\timerstate = Max(0, d\timerstate - FPSfactor)
							If d\timerstate + FPSfactor > 110 And d\timerstate <= 110 Then d\SoundCHN = PlaySound2(CautionSFX, Camera, d\obj)
							;If d\timerstate = 0 Then d\open = (Not d\open) : PlaySound2(CloseDoorSFX(Min(d\dir,1),Rand(0, 2)), Camera, d\obj)
							If d\timerstate = 0 Then d\open = (Not d\open) : d\SoundCHN = PlaySound2(CloseDoorSFX(d\dir,Rand(0, 2)), Camera, d\obj)
						EndIf
						If d\AutoClose And RemoteDoorOn = True Then
							If EntityDistanceSquared(Camera, d\obj) < PowTwo(2.1) Then
								If (Not Wearing714) Then PlaySound_Strict HorrorSFX(7)
								d\open = False : d\SoundCHN = PlaySound2(CloseDoorSFX(Min(d\dir,1), Rand(0, 2)), Camera, d\obj) : d\AutoClose = False
							EndIf
						EndIf				
					EndIf
				Else
					If d\openstate > 0 Then
						Select d\dir
							Case 0, 5
								d\openstate = Max(0, d\openstate - FPSfactor * 2 * (d\fastopen+1))
								MoveEntity(d\obj, Sin(d\openstate) * -FPSfactor * (d\fastopen+1) / 80.0, 0, 0)
								If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate) * (d\fastopen+1) * -FPSfactor / 80.0, 0, 0)	
							Case 1
								d\openstate = Max(0, d\openstate - FPSfactor*0.8)
								MoveEntity(d\obj, Sin(d\openstate) * -FPSfactor / 180.0, 0, 0)
								If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate) * FPSfactor / 180.0, 0, 0)
								If d\openstate < 15 And d\openstate+FPSfactor => 15
									If ParticleAmount=2
										For i = 0 To Rand(75,99)
											Local pvt% = CreatePivot()
											PositionEntity(pvt, EntityX(d\frameobj,True)+Rnd(-0.2,0.2), EntityY(d\frameobj,True)+Rnd(0.0,1.2), EntityZ(d\frameobj,True)+Rnd(-0.2,0.2))
											RotateEntity(pvt, 0, Rnd(360), 0)
											
											Local p.Particles = CreateParticle(EntityX(pvt), EntityY(pvt), EntityZ(pvt), 2, 0.002, 0, 300)
											p\speed = 0.005
											RotateEntity(p\pvt, Rnd(-20, 20), Rnd(360), 0)
											
											p\SizeChange = -0.00001
											p\size = 0.01
											ScaleSprite p\obj,p\size,p\size
											
											p\Achange = -0.01
											
											EntityOrder p\obj,-1
											
											FreeEntity pvt
										Next
									EndIf
								EndIf
							Case 2
								d\openstate = Max(0, d\openstate - FPSfactor * 2 * (d\fastopen+1))
								MoveEntity(d\obj, Sin(d\openstate) * -FPSfactor * (d\fastopen+1) / 85.0, 0, 0)
								If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate) * (d\fastopen+1) * -FPSfactor / 120.0, 0, 0)
							Case 3
								d\openstate = Max(0, d\openstate - FPSfactor * 2 * (d\fastopen+1))
								MoveEntity(d\obj, Sin(d\openstate) * -FPSfactor * (d\fastopen+1) / 162.0, 0, 0)
								If d\obj2 <> 0 Then MoveEntity(d\obj2, Sin(d\openstate) * (d\fastopen+1) * -FPSfactor / 162.0, 0, 0)
							Case 4 ;Used for 914 only
								d\openstate = Min(180, d\openstate - FPSfactor * 1.4)
								MoveEntity(d\obj, Sin(d\openstate) * -FPSfactor / 114.0, 0, 0)
						End Select
						
						If d\angle = 0 Lor d\angle=180 Then
							If Abs(EntityZ(d\frameobj, True)-EntityZ(Collider))<0.15 Then
								If Abs(EntityX(d\frameobj, True)-EntityX(Collider))<0.7*(d\dir*2+1) Then
									PositionEntity Collider, EntityX(Collider), EntityY(Collider), CurveValue(EntityZ(d\frameobj,True)+0.15*Sgn(EntityZ(Collider)-EntityZ(d\frameobj, True)), EntityZ(Collider), 5)
								EndIf
							EndIf
						Else
							If Abs(EntityX(d\frameobj, True)-EntityX(Collider))<0.15 Then	
								If Abs(EntityZ(d\frameobj, True)-EntityZ(Collider))<0.7*(d\dir*2+1) Then
									PositionEntity Collider, CurveValue(EntityX(d\frameobj,True)+0.15*Sgn(EntityX(Collider)-EntityX(d\frameobj, True)), EntityX(Collider), 5), EntityY(Collider), EntityZ(Collider)
								EndIf
							EndIf
						EndIf
						
						If d\DoorHitOBJ <> 0 Then
							ShowEntity d\DoorHitOBJ
						EndIf
					Else
						d\fastopen = 0
						PositionEntity(d\obj, EntityX(d\frameobj, True), EntityY(d\frameobj, True), EntityZ(d\frameobj, True))
						If d\obj2 <> 0 Then PositionEntity(d\obj2, EntityX(d\frameobj, True), EntityY(d\frameobj, True), EntityZ(d\frameobj, True))
						If d\obj2 <> 0 And d\dir = 0 Then
							MoveEntity(d\obj, 0, 0, 8.0 * RoomScale)
							MoveEntity(d\obj2, 0, 0, 8.0 * RoomScale)
						EndIf
						If d\DoorHitOBJ <> 0 Then
							HideEntity d\DoorHitOBJ
						EndIf
					EndIf
				EndIf
				
				If d\locked <> d\lockedupdated Then
					If d\locked Then
						For i = 0 To 1
							If d\buttons[i] <> 0 Then EntityTexture(d\buttons[i], ButtontextureLocked)
						Next
					Else
						For i = 0 To 1
							If d\buttons[i] <> 0 Then EntityTexture(d\buttons[i], ButtonTexture)
						Next
					EndIf
					d\lockedupdated = d\locked
				EndIf
				
			EndIf
			UpdateSoundOrigin(d\SoundCHN,Camera,d\frameobj)
			
			If d\dir = 1 And d\locked = 2 Then
				If d\openstate > 48.0 Then
					d\open = False
					d\openstate = Min(d\openstate, 48.0)
				EndIf	
			EndIf
			
			If d\DoorHitOBJ<>0 Then
				If DebugHUD Then
					EntityAlpha d\DoorHitOBJ,0.5
				Else
					EntityAlpha d\DoorHitOBJ,0.0
				EndIf
			EndIf
		Next
	EndIf
End Function

Function UseDoor(d.Doors, showmsg%=True, playsfx%=True)
	Local temp% = 0
	If d\KeyCard > 0 Then
		If SelectedItem = Null Then
			If showmsg = True Then
				If (MsgTimer<70*3) Lor (Not (Msg = GetLocalString("Messages", "keyinserted") Lor Msg = GetLocalString("Messages", "keyinsertednothing") Lor Msg = GetLocalStringR("Messages", "keylevel", "d\KeyCard"))) Then
					Msg = GetLocalString("Messages", "keyrequired")
					MsgTimer = 70 * 7
				EndIf
			EndIf
			Return
		Else
			Select SelectedItem\itemtemplate\tempname
				Case "key1"
					temp = 1
				Case "key2"
					temp = 2
				Case "key3"
					temp = 3
				Case "key4"
					temp = 4
				Case "key5"
					temp = 5
				Case "keyomni"
					temp = 6
				Default
					temp = -1
			End Select
			
			If temp =-1 Then 
				If showmsg = True Then
					If (MsgTimer<70*3) Lor (Not (Msg = GetLocalString("Messages", "keyinserted") Lor Msg = GetLocalString("Messages", "keyinsertednothing") Lor Msg = GetLocalStringR("Messages", "keylevel", "d\KeyCard"))) Then
						Msg = GetLocalString("Messages", "keyrequired")
						MsgTimer = 70 * 7
					EndIf
				EndIf
				Return				
			ElseIf temp >= d\KeyCard 
				SelectedItem = Null
				If showmsg = True Then
					If d\locked = True Then
						PlaySound_Strict KeyCardSFX2
						Msg = GetLocalString("Messages", "keyinsertednothing")
						MsgTimer = 70 * 7
						Return
					Else
						PlaySound_Strict KeyCardSFX1
						Msg = GetLocalString("Messages", "keyinserted")
						MsgTimer = 70 * 7	
					EndIf
				EndIf
			Else
				SelectedItem = Null
				If showmsg = True Then 
					PlaySound_Strict KeyCardSFX2					
					If d\locked = True Then
						Msg = GetLocalString("Messages", "keyinsertednothing")
					Else
						Msg = GetLocalStringR("Messages", "keylevel", d\KeyCard)
					EndIf
					MsgTimer = 70 * 7					
				EndIf
				Return
			EndIf
		EndIf	
	ElseIf d\KeyCard < 0
		;I can't find any way to produce short circuited boolean expressions so work around this by using a temporary variable - risingstar64
		;And now we have the capabilities to produce short circuited boolean expressions, oh how far we've come! ~Salvage
		If SelectedItem <> Null And ((SelectedItem\itemtemplate\tempname = "hand" And d\KeyCard=-1) Lor (SelectedItem\itemtemplate\tempname = "hand2" And d\KeyCard=-2) Lor (SelectedItem\itemtemplate\tempname = "hand3" And d\KeyCard=-3))
			PlaySound_Strict ScannerSFX1
			Msg = GetLocalString("Messages", "scannergranted")
			MsgTimer = 70 * 10
		Else
			If showmsg = True Then 
				PlaySound_Strict ScannerSFX2
				Msg = GetLocalString("Messages", "scannerdenied")
				MsgTimer = 70 * 10
			EndIf
			Return
		EndIf
		SelectedItem = Null
	Else
		If d\locked = True Then
			If showmsg = True Then 
				If Not (d\IsElevatorDoor>0) Then
					PlaySound_Strict ButtonSFX2
					If PlayerRoom\RoomTemplate\Name <> "room2elevator" Then
						If d\open Then
							Msg = GetLocalString("Messages", "elevnothing")
						Else	
							Msg = GetLocalString("Messages", "elevlocked")
						EndIf	
					Else
						Msg = GetLocalString("Messages", "elevbroken")
					EndIf
					MsgTimer = 70 * 5
				Else
					If d\IsElevatorDoor = 1 Then
						Msg = GetLocalString("Messages", "elevcall")
						MsgTimer = 70 * 5
					ElseIf d\IsElevatorDoor = 3 Then
						Msg = GetLocalString("Messages", "elevfloor")
						MsgTimer = 70 * 5
					ElseIf (Msg<>GetLocalString("Messages", "elevcall"))
						If (MsgTimer<70*3) Lor (Msg=Msg = GetLocalString("Messages", "elevalready"))
							Select Rand(10)
								Case 1
									Msg = GetLocalString("Messages", "elevspam1")
								Case 2
									Msg = GetLocalString("Messages", "elevspam2")
								Case 3
									Msg = GetLocalString("Messages", "elevspam3")
								Default
									Msg = GetLocalString("Messages", "elevalready")
							End Select
							MsgTimer = 70 * 7
						EndIf
					Else
						Msg = GetLocalString("Messages", "elevalready")
						MsgTimer = 70 * 7
					EndIf
				EndIf
				
			EndIf
			Return
		EndIf	
	EndIf
	
	d\open = (Not d\open)
	If d\LinkedDoor <> Null Then d\LinkedDoor\open = (Not d\LinkedDoor\open)
	
	If playsfx = True Then
		If d\open Then
			If d\LinkedDoor <> Null Then d\LinkedDoor\timerstate = d\LinkedDoor\timer
			d\timerstate = d\timer
			If d\dir = 1 And d\locked = 2 Then
				d\SoundCHN = PlaySound2(BigDoorErrorSFX(Rand(0, 2)), Camera, d\obj)
			Else
				d\SoundCHN = PlaySound2(OpenDoorSFX(d\dir, Rand(0, 2)), Camera, d\obj)
			EndIf
		Else
			d\SoundCHN = PlaySound2(CloseDoorSFX(d\dir, Rand(0, 2)), Camera, d\obj)
		EndIf
		UpdateSoundOrigin(d\SoundCHN, Camera, d\obj)
	Else
		If d\open Then
			If d\LinkedDoor <> Null Then d\LinkedDoor\timerstate = d\LinkedDoor\timer
			d\timerstate = d\timer
		EndIf
	EndIf
	
End Function

Function RemoveDoor(d.Doors)
	If d\buttons[0] <> 0 Then EntityParent d\buttons[0], 0
	If d\buttons[1] <> 0 Then EntityParent d\buttons[1], 0	
	
	If d\obj <> 0 Then FreeEntity d\obj
	If d\obj2 <> 0 Then FreeEntity d\obj2
	If d\frameobj <> 0 Then FreeEntity d\frameobj
	If d\buttons[0] <> 0 Then FreeEntity d\buttons[0]
	If d\buttons[1] <> 0 Then FreeEntity d\buttons[1]
	
	Delete d
End Function

DrawLoading(40,True)

Include "Source Code\Map.bb"

Include "Source Code\Save.bb"

DrawLoading(80,True)

Include "Source Code\NPCs.bb"

Include "Source Code\Events.bb"

Collisions HIT_PLAYER, HIT_MAP, 2, 2
Collisions HIT_PLAYER, HIT_PLAYER, 1, 3
Collisions HIT_ITEM, HIT_MAP, 2, 2
Collisions HIT_APACHE, HIT_APACHE, 1, 2
Collisions HIT_178, HIT_MAP, 2, 2
Collisions HIT_178, HIT_178, 1, 3
Collisions HIT_DEAD, HIT_MAP, 2, 2

DrawLoading(90, True)

;----------------------------------- meshes and textures ----------------------------------------------------------------

Global FogTexture%, Fog%
Global GasMaskOverlay%
Global HazmatOverlay%
Global InfectOverlay%
Global NVTexture%, NVOverlay%
Global GlassesOverlay%
Global DarkTexture%, Dark%
Global Collider%
Global Head%

Global FogNVTexture%

Global TeslaTexture%

Global LightTexture%, Light%
Global LightSpriteTex%[3]
Global DoorOBJ%, DoorFrameOBJ%

Global LeverOBJ%, LeverBaseOBJ%

Global DoorColl%
Global ButtonOBJ%, ButtonKeyOBJ%, ButtonCodeOBJ%, ButtonScannerOBJ%, ButtonElevatorOBJ%

Dim DecalTextures%(20)

Global Monitor%, MonitorTexture%
Global CamBaseOBJ%, CamOBJ%

Global LiquidObj%,MTFObj%,GuardObj%,ClassDObj%,VehicleObj%
Global ApacheObj%,ApacheRotorObj%

Global UnableToMove% = False
Global ShouldEntitiesFall% = True
Global PlayerFallingPickDistance# = 10.0

Global MTF_CameraCheckTimer# = 0.0
Global MTF_CameraCheckDetected% = False

;---------------------------------------------------------------------------------------------------

Include "Source Code\Menu.bb"
MainMenuOpen = True

;---------------------------------------------------------------------------------------------------

FlushKeys()
FlushMouse()

DrawLoading(100, True)

LoopDelay = MilliSecs()

Global UpdateParticles_Time# = 0.0

Global CurrTrisAmount%

Global Input_ResetTime# = 0

Type SCP427
	Field Using%
	Field Timer#
	Field Sound[2]
	Field SoundCHN[2]
	Field Amount%
End Type

Global I_427.SCP427 = New SCP427

;----------------------------------------------------------------------------------------------------------------------------------------------------
; MAIN LOOP
;----------------------------------------------------------------------------------------------------------------------------------------------------

Const TICK_DURATION# = 70.0 / 60.0

Type FixedTimesteps
	Field accumulator#
	Field prevTime%
	Field currTime%
	Field fps%
	Field tempfps%
	Field fpsgoal%
End Type

Global ft.FixedTimesteps = New FixedTimesteps

Repeat
	
	Cls
	
	Local elapsedMilliseconds%
	ft\currTime = MilliSecs()
	elapsedMilliseconds = ft\currTime-ft\prevTime
	If (elapsedMilliseconds>0 And elapsedMilliseconds<500) Then
		ft\accumulator = ft\accumulator+Max(0,Float(elapsedMilliseconds)*70.0/1000.0)
	EndIf
	ft\prevTime = ft\currTime
	
	If Framelimit > 0 Then
		;Framelimit
		Local WaitingTime% = (1000.0 / Framelimit) - (MilliSecs() - LoopDelay)
		Delay WaitingTime%
		
		LoopDelay = MilliSecs()
	EndIf
	
	FPSfactor = TICK_DURATION
	FPSfactor2 = FPSfactor
	
	If MenuOpen Lor InvOpen Lor OtherOpen<>Null Lor ConsoleOpen Lor SelectedDoor <> Null Lor SelectedScreen <> Null Lor Using294 Then FPSfactor = 0
	
	UpdateMusic()
	If EnableSFXRelease Then AutoReleaseSounds()
	
	If MainMenuOpen Then
		If ShouldPlay = 21 Then
			EndBreathSFX = LoadSound("SFX\Ending\MenuBreath.ogg")
			EndBreathCHN = PlaySound(EndBreathSFX)
			ShouldPlay = 66
		ElseIf ShouldPlay = 66
			If (Not ChannelPlaying(EndBreathCHN)) Then
				FreeSound(EndBreathSFX)
				ShouldPlay = 11
			EndIf
		Else
			ShouldPlay = 11
		EndIf
		UpdateMainMenu()
	Else
		While (ft\accumulator>0.0)
			ft\accumulator = ft\accumulator-TICK_DURATION
			If (ft\accumulator<=0.0) Then CaptureWorld()
			
			UpdateStreamSounds()
			
			ShouldPlay = Min(PlayerZone,2)
			
			DrawHandIcon = False
			
			RestoreSanity = True
			ShouldEntitiesFall = True
			
			If Input_ResetTime<=0.0
				DoubleClick = False
				MouseHit1 = MouseHit(1)
				If MouseHit1 Then
					If MilliSecs() - LastMouseHit1 < 800 Then DoubleClick = True
					LastMouseHit1 = MilliSecs()
				EndIf
				
				Local prevmousedown1 = MouseDown1
				MouseDown1 = MouseDown(1)
				If prevmousedown1 = True And MouseDown1=False Then MouseUp1 = True Else MouseUp1 = False
				
				MouseHit2 = MouseHit(2)
				
				If (Not MouseDown1) And (Not MouseHit1) Then GrabbedEntity = 0
			Else
				Input_ResetTime = Max(Input_ResetTime-FPSfactor,0.0)
			EndIf
			
			If PlayerRoom\RoomTemplate\Name <> "pocketdimension" And PlayerRoom\RoomTemplate\Name <> "gatea" And (PlayerRoom\RoomTemplate\Name <> "gateb" And EntityY(Collider) =< 1040.0 * RoomScale) And (Not MenuOpen) And (Not ConsoleOpen) And (Not InvOpen) Then
				
				If Rand(1500) = 1 Then
					For i = 0 To 5
						If AmbientSFX(i,CurrAmbientSFX)<>0 Then
							If ChannelPlaying(AmbientSFXCHN)=0 Then FreeSound_Strict AmbientSFX(i,CurrAmbientSFX) : AmbientSFX(i,CurrAmbientSFX) = 0
						EndIf			
					Next
					
					PositionEntity (SoundEmitter, EntityX(Camera) + Rnd(-1.0, 1.0), 0.0, EntityZ(Camera) + Rnd(-1.0, 1.0))
					
					If Rand(3)=1 Then PlayerZone = 3
					
					If PlayerRoom\RoomTemplate\Name = "room173" Then
						PlayerZone = 4
					ElseIf PlayerRoom\RoomTemplate\Name = "room860"
						For e.Events = Each Events
							If e\EventName = "room860"
								If e\EventState = 1.0
									PlayerZone = 5
									PositionEntity (SoundEmitter, EntityX(SoundEmitter), 30.0, EntityZ(SoundEmitter))
								EndIf
								
								Exit
							EndIf
						Next
					EndIf
					
					CurrAmbientSFX = Rand(0,AmbientSFXAmount(PlayerZone)-1)
					
					Select PlayerZone
						Case 0,1,2
							If AmbientSFX(PlayerZone,CurrAmbientSFX)=0 Then AmbientSFX(PlayerZone,CurrAmbientSFX)=LoadSound_Strict("SFX\Ambient\Zone"+(PlayerZone+1)+"\ambient"+(CurrAmbientSFX+1)+".ogg")
						Case 3
							If AmbientSFX(PlayerZone,CurrAmbientSFX)=0 Then AmbientSFX(PlayerZone,CurrAmbientSFX)=LoadSound_Strict("SFX\Ambient\General\ambient"+(CurrAmbientSFX+1)+".ogg")
						Case 4
							If AmbientSFX(PlayerZone,CurrAmbientSFX)=0 Then AmbientSFX(PlayerZone,CurrAmbientSFX)=LoadSound_Strict("SFX\Ambient\Pre-breach\ambient"+(CurrAmbientSFX+1)+".ogg")
						Case 5
							If AmbientSFX(PlayerZone,CurrAmbientSFX)=0 Then AmbientSFX(PlayerZone,CurrAmbientSFX)=LoadSound_Strict("SFX\Ambient\Forest\ambient"+(CurrAmbientSFX+1)+".ogg")
					End Select
					
					AmbientSFXCHN = PlaySound2(AmbientSFX(PlayerZone,CurrAmbientSFX), Camera, SoundEmitter)
				EndIf
				UpdateSoundOrigin(AmbientSFXCHN,Camera, SoundEmitter)
				
				If Rand(50000) = 3 Then
					Local RN$ = PlayerRoom\RoomTemplate\Name$
					If RN$ <> "room860" And RN$ <> "room1123" And RN$ <> "room173" And RN$ <> "dimension1499" Then
						If FPSfactor > 0 Then LightBlink = Rnd(1.0,2.0)
						PlaySound_Strict  LoadTempSound("SFX\SCP\079\Broadcast"+Rand(1,7)+".ogg")
					EndIf 
				EndIf
			EndIf
			
			UpdateCheckpoint1 = False
			UpdateCheckpoint2 = False
			
			If (Not MenuOpen) And (Not InvOpen) And (OtherOpen=Null) And (SelectedDoor = Null) And (ConsoleOpen = False) And (Using294 = False) And (SelectedScreen = Null) And EndingTimer=>0 Then
				LightVolume = CurveValue(TempLightVolume, LightVolume, 50.0)
				CameraFogRange(Camera, CameraFogNear*LightVolume,CameraFogFar*LightVolume)
				CameraFogColor(Camera, 0,0,0)
				CameraFogMode Camera,1
				CameraRange(Camera, 0.05, Min(CameraFogFar*LightVolume*1.5,28))	
				If PlayerRoom\RoomTemplate\Name<>"pocketdimension" Then
					CameraClsColor(Camera, 0,0,0)
				EndIf
				
				AmbientLight Brightness, Brightness, Brightness	
				PlayerSoundVolume = CurveValue(0.0, PlayerSoundVolume, 5.0)
				
				CanSave% = True
				UpdateDeafPlayer()
				UpdateEmitters()
				MouseLook()
				If PlayerRoom\RoomTemplate\Name = "dimension1499" And QuickLoad_CurrEvent <> Null
					ShouldEntitiesFall = False
				EndIf
				MovePlayer()
				If (Not Freeze)
					InFacility = CheckForPlayerInFacility()
					If PlayerRoom\RoomTemplate\Name = "dimension1499"
						If QuickLoad_CurrEvent = Null Then
							UpdateDimension1499()
						EndIf
						UpdateLeave1499()
					ElseIf PlayerRoom\RoomTemplate\Name = "gatea" Lor (PlayerRoom\RoomTemplate\Name="gateb" And EntityY(Collider)>1040.0*RoomScale-2000)
						UpdateDoors()
						If QuickLoad_CurrEvent = Null Then
							UpdateEndings()
						EndIf
						UpdateScreens()
						UpdateRoomLights(Camera)
					Else
						UpdateSecurityCams()
						UpdateDoors()
						If QuickLoad_CurrEvent = Null Then
							UpdateEvents()
						EndIf
						UpdateScreens()
						TimeCheckpointMonitors()
						Update294()
						UpdateRoomLights(Camera)
					EndIf
					UpdateFluLights()
					UpdateDecals()
					UpdateMTF()
					UpdateNPCs()
					UpdateItems()
					UpdateParticles()
					Use427()
					UpdateMonitorSaving()
					;Added a simple code for updating the Particles function depending on the FPSFactor (still WIP, might not be the final version of it) - ENDSHN
					UpdateParticles_Time# = Min(1,UpdateParticles_Time#+FPSfactor)
					If UpdateParticles_Time#=1
						UpdateDevilEmitters()
						UpdateParticles_Devil()
						UpdateParticles_Time#=0
					EndIf
				Else
					UpdateDoors()
					UpdateRooms()
				EndIf
			EndIf
			
			If I_Cheats\InfiniteStamina Then Stamina = 100.0
			If I_Cheats\NoBlink Then BlinkTimer = BLINKFREQ
			
			If FPSfactor=0
				UpdateWorld(0)
			Else
				UpdateWorld()
				ManipulateNPCBones()
			EndIf
		
			Local darkA# = 0.0
			If (Not MenuOpen)  Then
				If Sanity < 0 Then
					If RestoreSanity Then Sanity = Min(Sanity + FPSfactor, 0.0)
					If Sanity < (-200) Then 
						darkA = Max(Min((-Sanity - 200) / 700.0, 0.6), darkA)
						If KillTimer => 0 Then 
							HeartBeatVolume = Min(Abs(Sanity+200)/500.0,1.0)
							HeartBeatRate = Max(70 + Abs(Sanity+200)/6.0,HeartBeatRate)
						EndIf
					EndIf
				EndIf
				
				If EyeStuck > 0 Then 
					BlinkTimer = BLINKFREQ
					EyeStuck = Max(EyeStuck-FPSfactor,0)
					
					If EyeStuck < 9000 Then BlurTimer = Max(BlurTimer, (9000-EyeStuck)*0.5)
					If EyeStuck < 6000 Then darkA = Min(Max(darkA, (6000-EyeStuck)/5000.0),1.0)
					If EyeStuck < 9000 And EyeStuck+FPSfactor =>9000 Then 
						Msg = GetLocalString("Messages", "eyedroptears")
						MsgTimer = 70*6
					EndIf
				EndIf
				
				If BlinkTimer < 0 Then
					If BlinkTimer > - 5 Then
						darkA = Max(darkA, Sin(Abs(BlinkTimer * 18.0)))
					ElseIf BlinkTimer > - 15
						darkA = 1.0
					Else
						darkA = Max(darkA, Abs(Sin(BlinkTimer * 18.0)))
					EndIf
					
					If BlinkTimer <= - 20 Then
						;Randomizes the frequency of blinking. Scales with difficulty.
						Select SelectedDifficulty\otherFactors
							Case EASY
								BLINKFREQ = Rnd(490,700)
							Case NORMAL
								BLINKFREQ = Rnd(455,665)
							Case HARD
								BLINKFREQ = Rnd(420,630)
							Case EXTREME
								BLINKFREQ = Rnd(200, 400)
						End Select 
						BlinkTimer = BLINKFREQ
					EndIf
					
					BlinkTimer = BlinkTimer - FPSfactor
				Else
					If Wearing178 > 0
						BlinkTimer = BlinkTimer - FPSfactor * 0.6 * BlinkEffect / (Wearing178+1)
					Else
						BlinkTimer = BlinkTimer - FPSfactor * 0.6 * BlinkEffect
					EndIf
					If EyeIrritation > 0 Then BlinkTimer=BlinkTimer-Min(EyeIrritation / 100.0 + 1.0, 4.0) * FPSfactor
					
					darkA = Max(darkA, 0.0)
				EndIf
				
				EyeIrritation = Max(0, EyeIrritation - FPSfactor)
				
				If BlinkEffectTimer > 0 Then
					BlinkEffectTimer = BlinkEffectTimer - (FPSfactor/70)
				Else
					If BlinkEffect <> 1.0 Then BlinkEffect = 1.0
				EndIf
				
				LightBlink = Max(LightBlink - (FPSfactor / 35.0), 0)
				If LightBlink > 0.0 And WearingNightVision =< 0 Then darkA = Min(Max(darkA, LightBlink * Rnd(0.3, 0.8)), 1.0)
				
				If Using294 Then darkA=1.0
				
				If (WearingNightVision > 0) Then darkA = Max((1.0-SecondaryLightOn)*0.9, darkA)
				
				If KillTimer < 0 Then
					InvOpen = False
					SelectedItem = Null
					SelectedScreen = Null
					SelectedMonitor = Null
					BlurTimer = Abs(KillTimer*5)
					KillTimer=KillTimer-(FPSfactor*0.8)
					If KillTimer < - 360 Then 
						MenuOpen = True 
						If SelectedEnding <> "" Then EndingTimer = Min(KillTimer,-0.1)
					EndIf
					darkA = Max(darkA, Min(Abs(KillTimer / 400.0), 1.0))
				EndIf
				
				If FallTimer < 0 Then
					If SelectedItem <> Null Then
						If Instr(SelectedItem\itemtemplate\tempname,"hazmat") Lor Instr(SelectedItem\itemtemplate\tempname,"vest") Then
							If WearingHazmat=0 And WearingVest=0 Then
								DropItem(SelectedItem)
							EndIf
						EndIf
					EndIf
					InvOpen = False
					SelectedItem = Null
					SelectedScreen = Null
					SelectedMonitor = Null
					BlurTimer = Abs(FallTimer*10)
					FallTimer = FallTimer-FPSfactor
					darkA = Max(darkA, Min(Abs(FallTimer / 400.0), 1.0))				
				EndIf
				
				If SelectedItem <> Null And (SelectedItem\itemtemplate\tempname = "badnav" Lor SelectedItem\itemtemplate\tempname = "nav" Lor SelectedItem\itemtemplate\tempname = "navulti" Lor SelectedItem\itemtemplate\tempname = "nav310" Lor SelectedItem\itemtemplate\tempname = "nav300") Lor SelectedScreen <> Null Then
					darkA = Max(darkA, 0.5)
				EndIf
				
				EntityAlpha(Dark, darkA)	
			EndIf
			
			If LightFlash > 0 Then
				ShowEntity Light
				EntityAlpha(Light, Max(Min(LightFlash + Rnd(-0.2, 0.2), 1.0), 0.0))
				LightFlash = Max(LightFlash - (FPSfactor / 70.0), 0)
			Else
				HideEntity Light
				;EntityAlpha(Light, LightFlash)
			EndIf
			
			EntityColor Light,255,255,255
			
		Wend
		

		
		If KeyHit(I_Keys\INV) And VomitTimer >= 0 Then
			If (Not UnableToMove) And (Not IsZombie) And (Not Using294) Then
				Local W$ = ""
				Local V# = 0
				If SelectedItem<>Null
					W$ = SelectedItem\itemtemplate\tempname
					V# = SelectedItem\state
					If SelectedItem\itemtemplate\tempname = "scp1025" Then ;Otherwise opening inv avoids SCP-1025 reset
						If SelectedItem\itemtemplate\img<>0 Then FreeImage(SelectedItem\itemtemplate\img)
						SelectedItem\itemtemplate\img=0
					EndIf
				EndIf
				If V=0 Lor V=100 Lor (W<>"badvest" And W<>"vest" And W<>"finevest" And W<>"hazmat0" And W<>"hazmat" And W<>"hazmat2" And W<>"hazmat3")
					If InvOpen Then
						ResumeSounds()
						MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
					Else
						DoubleClickSlot = -1
						PauseSounds()
					EndIf
					InvOpen = Not InvOpen
					If OtherOpen<>Null Then OtherOpen=Null
					SelectedItem = Null
				EndIf
			EndIf
		EndIf
		
		If KeyHit(I_Keys\SAVE) Then
			If SelectedDifficulty\saveType = SAVEANYWHERE Then
				RN$ = PlayerRoom\RoomTemplate\Name$
				CreateConsoleMsg(RN)
				If RN$ = "room173" Lor RN$ = "dimension1499" Lor RN$ = "gatea" Lor (RN$ = "gateb" And EntityY(Collider)>1040.0*RoomScale) Then
					Msg = GetLocalString("Messages", "savecantloc")
					MsgTimer = 70 * 4
				ElseIf (Not CanSave) Lor QuickLoad_CurrEvent <> Null
					Msg = GetLocalString("Messages", "savecantmom")
					MsgTimer = 70 * 4
					If QuickLoad_CurrEvent <> Null Then
						Msg = Msg + " " + GetLocalString("Messages", "saveload")
					EndIf
				Else
					SaveGame(CurrSave\Name)
				EndIf
			ElseIf SelectedDifficulty\saveType = SAVEONSCREENS
				If SelectedScreen=Null And SelectedMonitor=Null Then
					Msg = GetLocalString("Messages", "savemonitors")
					MsgTimer = 70 * 4
				Else
					RN$ = PlayerRoom\RoomTemplate\Name$
					If RN$ = "room173" Lor (RN$ = "gateb" And EntityY(Collider)>1040.0*RoomScale) Lor RN$ = "gatea" Then
						Msg = GetLocalString("Messages", "savecantloc")
						MsgTimer = 70 * 4
					ElseIf (Not CanSave) Lor QuickLoad_CurrEvent <> Null
						Msg = GetLocalString("Messages", "savecantmom")
						MsgTimer = 70 * 4
						If QuickLoad_CurrEvent <> Null Then
							Msg = Msg + " " + GetLocalString("Messages", "saveload")
						EndIf
					Else
						If SelectedScreen<>Null
							GameSaved = False
							Playable = True
							DropSpeed = 0
						EndIf
						SaveGame(CurrSave\Name)
					EndIf
				EndIf
			Else
				Msg = GetLocalString("Messages", "savedisabled")
				MsgTimer = 70 * 4
			EndIf
		ElseIf SelectedDifficulty\saveType = SAVEONSCREENS And (SelectedScreen<>Null Lor SelectedMonitor<>Null)
			If (Msg<>GetLocalString("Messages", "saved") And Msg<>GetLocalString("Messages", "savecantloc") And Msg<>GetLocalString("Messages", "savecantmom")) Lor MsgTimer<=0 Then
				Msg = GetLocalStringR("Messages", "savepress", I_Keys\KeyName[I_Keys\SAVE])
				MsgTimer = 70*4
			EndIf
			
			If MouseHit2 Then SelectedMonitor = Null
		EndIf
		
		If KeyHit(I_Keys\CONSOLE) Then
			If I_Opt\ConsoleEnabled
				If ConsoleOpen Then
					UsedConsole = True
					ResumeSounds()
					MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
				Else
					PauseSounds()
				EndIf
				ConsoleOpen = (Not ConsoleOpen)
				FlushKeys()
			EndIf
		EndIf
		
		RenderWorld2(Max(0.0,1.0+(ft\accumulator/TICK_DURATION)))
		
		BlurVolume = Min(CurveValue(0.0, BlurVolume, 20.0),0.95)
		If BlurTimer > 0.0 Then
			BlurVolume = Max(Min(0.95, BlurTimer / 1000.0), BlurVolume)
			BlurTimer = Max(BlurTimer - FPSfactor, 0.0)
		EndIf
		
		UpdateBlur(BlurVolume)
		
		DrawGUI()
		
		If EndingTimer < 0 Then
			If SelectedEnding <> "" Then DrawEnding()
		Else
			DrawMenu()			
		EndIf
		
		UpdateConsole()
		
		If PlayerRoom <> Null Then
			If PlayerRoom\RoomTemplate\Name = "room173" Then
				For e.Events = Each Events
					If e\EventName = "173" Then
						If e\EventState3 => 40 And e\EventState3 < 50 Then
							If InvOpen Then
								Msg = GetLocalString("Messages", "opendoc")
								MsgTimer=70*7
								e\EventState3 = 50
							EndIf
						EndIf
					EndIf
				Next
			EndIf
		EndIf
		
		If MsgTimer > 0 And MenuOpen = 0 Then
			Local temp% = False
			If (Not (InvOpen Lor OtherOpen<>Null)) And SelectedItem <> Null And SelectedItem\itemtemplate\tempname = "paper" Then
				temp% = True
			EndIf
			
			Local temp2% = Min(MsgTimer / 2, 255)
			
			Color 0,0,0
			If (Not temp%)
				Text((I_Opt\GraphicWidth / 2)+1, (I_Opt\GraphicHeight * 0.5) + 201, Msg, True)
				Color temp2, temp2, temp2
				Text((I_Opt\GraphicWidth / 2), (I_Opt\GraphicHeight / 2) + 200, Msg, True)
			Else
				Text((I_Opt\GraphicWidth / 2)+1, (I_Opt\GraphicHeight * 0.94) + 1, Msg, True)
				Color temp2, temp2, temp2
				Text((I_Opt\GraphicWidth / 2), (I_Opt\GraphicHeight * 0.94), Msg, True)
			EndIf
			MsgTimer=MsgTimer-FPSfactor2
		EndIf
		
		Color 255, 255, 255
		If I_Opt\ShowFPS Then SetFont I_Opt\Fonts[0] : Text(0, 0, "FPS: " + ft\fps) : SetFont I_Opt\Fonts[1]
		
		If QuickLoad_CurrEvent <> Null
			QuickLoadEvents()
		EndIf
		
		UpdateAchievementMsg()
	EndIf
	
	If I_Opt\GraphicMode = 1 Then
		If (RealGraphicWidth<>I_Opt\GraphicWidth) Lor (RealGraphicHeight<>I_Opt\GraphicHeight) Then
			SetBuffer TextureBuffer(fresize_texture)
			ClsColor 0,0,0 : Cls
			CopyRect 0,0,I_Opt\GraphicWidth,I_Opt\GraphicHeight,1024-I_Opt\GraphicWidth/2,1024-I_Opt\GraphicHeight/2,BackBuffer(),TextureBuffer(fresize_texture)
			SetBuffer BackBuffer()
			ClsColor 0,0,0 : Cls
			ScaleRender(0,0,2048.0 / Float(I_Opt\GraphicWidth) * AspectRatioRatio, 2048.0 / Float(I_Opt\GraphicWidth) * AspectRatioRatio)
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
	
	;CatchErrors("Uncaught Main loop")
	
	If I_Opt\ShowFPS Then
		If ft\fpsgoal < MilliSecs() Then
			ft\fps = ft\tempfps
			ft\tempfps = 0
			ft\fpsgoal = MilliSecs() + 1000
		Else
			ft\tempfps = ft\tempfps + 1
		EndIf
	EndIf
	
	If Vsync = 0 Then
		Flip 0
	Else 
		Flip 1
	EndIf
Forever

;----------------------------------------------------------------------------------------------------------------------------------------------------
;----------------------------------------------------------------------------------------------------------------------------------------------------
;----------------------------------------------------------------------------------------------------------------------------------------------------

Function Kill()
	If I_Cheats\GodMode Then Return
	
	If BreathCHN <> 0 Then
		If ChannelPlaying(BreathCHN) Then StopChannel(BreathCHN)
	EndIf
	
	If KillTimer >= 0 Then
		KillAnim = Rand(0,1)
		PlaySound_Strict(DamageSFX(0))
		If SelectedDifficulty\permaDeath Then
			DeleteGame(CurrSave)
		EndIf
		
		KillTimer = Min(-1, KillTimer)
		ShowEntity Head
		PositionEntity(Head, EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True), True)
		ResetEntity (Head)
		RotateEntity(Head, 0, EntityYaw(Camera), 0)		
	EndIf
End Function

Function DrawEnding()
	
	ShowPointer()
	
	FPSfactor = 0
	;EndingTimer=EndingTimer-FPSfactor2
	If EndingTimer>-2000
		EndingTimer=Max(EndingTimer-FPSfactor2,-1111)
	Else
		EndingTimer=EndingTimer-FPSfactor2
	EndIf
	
	GiveAchievement(Achv055)
	If (Not UsedConsole) Then GiveAchievement(AchvConsole)
	If SelectedDifficulty\name = GetLocalString("Menu", "keter") Then
		GiveAchievement(AchvKeter)
		file$ = WriteFile("does the black moon howl")
		WriteString(file, "when the foundation crumbles")
		CloseFile(file)
		ApolUnlocked = True
	EndIf
	Local x,y,width,height, temp
	Local itt.ItemTemplates, r.Rooms
	
	Select Lower(SelectedEnding)
		Case "b2", "a1"
			ClsColor Max(255+(EndingTimer)*2.8,0), Max(255+(EndingTimer)*2.8,0), Max(255+(EndingTimer)*2.8,0)
		Default
			ClsColor 0,0,0
	End Select
	
	ShouldPlay = 66
	
	Cls
	
	If EndingTimer<-200 Then
		
		If BreathCHN <> 0 Then
			If ChannelPlaying(BreathCHN) Then StopChannel BreathCHN : Stamina = 100
		EndIf
		
		;If EndingTimer <-400 Then 
		;	ShouldPlay = 13
		;EndIf
		
		If EndingScreen = 0 Then
			EndingScreen = LoadImage_Strict("GFX\endingscreen.pt")
			
			ShouldPlay = 23
			CurrMusicVolume = MusicVolume
			
			CurrMusicVolume = MusicVolume
			StopStream_Strict(MusicCHN)
			MusicCHN = StreamSound_Strict("SFX\Music\"+Music[23]+".ogg",CurrMusicVolume,0)
			NowPlaying = ShouldPlay
			
			PlaySound_Strict LightSFX
		EndIf
		
		If EndingTimer > -700 Then 
			
			;-200 -> -700
			;Max(50 - (Abs(KillTimer)-200),0)	=	0->50
			If Rand(1,150)<Min((Abs(EndingTimer)-200),155) Then
				DrawImage EndingScreen, I_Opt\GraphicWidth/2-400, I_Opt\GraphicHeight/2-400
			Else
				Color 0,0,0
				Rect 100,100,I_Opt\GraphicWidth-200,I_Opt\GraphicHeight-200
				Color 255,255,255
			EndIf
			
			If EndingTimer+FPSfactor2 > -450 And EndingTimer <= -450 Then
				Select Lower(SelectedEnding)
					Case "a1", "a2"
						PlaySound_Strict LoadTempSound("SFX\Ending\GateA\Ending"+SelectedEnding+".ogg")
					Case "b1", "b2", "b3"
						PlaySound_Strict LoadTempSound("SFX\Ending\GateB\Ending"+SelectedEnding+".ogg")
				End Select
			EndIf			
			
		Else
			
			DrawImage EndingScreen, I_Opt\GraphicWidth/2-400, I_Opt\GraphicHeight/2-400
			
			If EndingTimer < -1000 And EndingTimer > -2000
				
				width = ImageWidth(PauseMenuIMG)
				height = ImageHeight(PauseMenuIMG)
				x = I_Opt\GraphicWidth / 2 - width / 2
				y = I_Opt\GraphicHeight / 2 - height / 2
				
				DrawImage PauseMenuIMG, x, y
				
				Color(255, 255, 255)
				SetFont I_Opt\Fonts[2]
				Text(x + width / 2 + 40*MenuScale, y + 20*MenuScale, GetLocalString("Menu", "end"), True)
				SetFont I_Opt\Fonts[1]
				
				If AchievementsMenu=0 Then 
					x = x+132*MenuScale
					y = y+122*MenuScale
					
					Local roomamount = 0, roomsfound = 0
					For r.Rooms = Each Rooms
						roomamount = roomamount + 1
						roomsfound = roomsfound + r\found
					Next
					
					Local docamount=0, docsfound=0
					For itt.ItemTemplates = Each ItemTemplates
						If itt\tempname = "paper" Then
							docamount=docamount+1
							docsfound=docsfound+itt\found
						EndIf
					Next
					
					Local scpsEncountered=1
					For i = 0 To 24
						scpsEncountered = scpsEncountered+Achievements[i]
					Next
					
					Local achievementsUnlocked =0
					For i = 0 To MAXACHIEVEMENTS-1
						achievementsUnlocked = achievementsUnlocked + Achievements[i]
					Next
					
					Text x, y, "SCPs encountered: " +scpsEncountered
					Text x, y+20*MenuScale, "Achievements unlocked: " + achievementsUnlocked+"/"+(MAXACHIEVEMENTS)
					Text x, y+40*MenuScale, "Rooms found: " + roomsfound+"/"+roomamount
					Text x, y+60*MenuScale, "Documents discovered: " +docsfound+"/"+docamount
					Text x, y+80*MenuScale, "Items refined in SCP-914: " +RefinedItems			
					
					x = I_Opt\GraphicWidth / 2 - width / 2
					y = I_Opt\GraphicHeight / 2 - height / 2
					x = x+width/2
					y = y+height-100*MenuScale
					
					If DrawButton(x-145*MenuScale,y-200*MenuScale,390*MenuScale,60*MenuScale,Upper(GetLocalString("Menu", "ach")), True) Then
						AchievementsMenu = 1
					EndIf
					
					If DrawButton(x-145*MenuScale,y-100*MenuScale,390*MenuScale,60*MenuScale,GetLocalString("Menu", "mainmenu"), True)
						ShouldPlay = 24
						NowPlaying = ShouldPlay
						For i=0 To 9
							If TempSounds[i]<>0 Then FreeSound_Strict TempSounds[i] : TempSounds[i]=0
						Next
						StopStream_Strict(MusicCHN)
						MusicCHN = StreamSound_Strict("SFX\Music\"+Music[NowPlaying]+".ogg",0.0,2)
						SetStreamVolume_Strict(MusicCHN,1.0*MusicVolume)
						FlushKeys()
						EndingTimer=-2000
						InitCredits()
					EndIf
				Else
					ShouldPlay = 23
					DrawMenu()
				EndIf
			;Credits
			ElseIf EndingTimer<=-2000
				ShouldPlay = 24
				DrawCredits()
			EndIf
			
		EndIf
		
	EndIf
	
	If I_Opt\GraphicMode = 0 Then DrawImage CursorIMG, ScaledMouseX(),ScaledMouseY()
	
	SetFont I_Opt\Fonts[1]
End Function

Type CreditsLine
	Field txt$
	Field id%
	Field stay%
End Type

Global CreditsTimer# = 0.0
Global CreditsScreen%

Function InitCredits()

	Local cl.CreditsLine
	Local file% = OpenFile("Credits.txt")
	Local l$
	
	I_Opt\Fonts[6] = LoadLocalFont("CreditsFont1")
	I_Opt\Fonts[7] = LoadLocalFont("CreditsFont2")
	
	If CreditsScreen = 0
		CreditsScreen = LoadImage_Strict("GFX\creditsscreen.pt")
	EndIf
	
	Repeat
		l = ReadLine(file)
		cl = New CreditsLine
		cl\txt = l
	Until Eof(file)
	
	Delete First CreditsLine
	CreditsTimer = 0
	
End Function

Function DrawCredits()
	
	Local credits_Y# = (EndingTimer+2000)/2+(I_Opt\GraphicHeight+10)
	Local cl.CreditsLine
	Local id%
	Local endlinesamount%
	Local LastCreditLine.CreditsLine
	
	Cls
	
	If Rand(1,300)>1
		DrawImage CreditsScreen, I_Opt\GraphicWidth/2-400, I_Opt\GraphicHeight/2-400
	EndIf
	
	id = 0
	endlinesamount = 0
	LastCreditLine = Null
	Color 255,255,255
	For cl = Each CreditsLine
		cl\id = id
		If Left(cl\txt,1)="*"
			SetFont I_Opt\Fonts[7]
			If cl\stay=False
				Text I_Opt\GraphicWidth/2,credits_Y+(24*cl\id*MenuScale),Right(cl\txt,Len(cl\txt)-1),True
			EndIf
		ElseIf Left(cl\txt,1)="/"
			LastCreditLine = Before(cl)
		Else
			SetFont I_Opt\Fonts[6]
			If cl\stay=False
				Text I_Opt\GraphicWidth/2,credits_Y+(24*cl\id*MenuScale),cl\txt,True
			EndIf
		EndIf
		If LastCreditLine<>Null
			If cl\id>LastCreditLine\id
				cl\stay = True
			EndIf
		EndIf
		If cl\stay
			endlinesamount=endlinesamount+1
		EndIf
		id=id+1
	Next
	If (credits_Y+(24*LastCreditLine\id*MenuScale))<-StringHeight(LastCreditLine\txt)
		CreditsTimer=CreditsTimer+(0.5*FPSfactor2)
		If CreditsTimer>=0.0 And CreditsTimer<255.0
			Color Max(Min(CreditsTimer,255),0),Max(Min(CreditsTimer,255),0),Max(Min(CreditsTimer,255),0)
		ElseIf CreditsTimer>=255.0
			Color 255,255,255
			If CreditsTimer>500.0
				CreditsTimer=-255.0
			EndIf
		Else
			Color Max(Min(-CreditsTimer,255),0),Max(Min(-CreditsTimer,255),0),Max(Min(-CreditsTimer,255),0)
			If CreditsTimer>=-1.0
				CreditsTimer=-1.0
			EndIf
		EndIf
		DebugLog CreditsTimer
	EndIf
	If CreditsTimer<>0.0
		For cl = Each CreditsLine
			If cl\stay
				SetFont I_Opt\Fonts[6]
				If Left(cl\txt,1)="/"
					Text I_Opt\GraphicWidth/2,(I_Opt\GraphicHeight/2)+(endlinesamount/2)+(24*cl\id*MenuScale),Right(cl\txt,Len(cl\txt)-1),True
				Else
					Text I_Opt\GraphicWidth/2,(I_Opt\GraphicHeight/2)+(24*(cl\id-LastCreditLine\id)*MenuScale)-((endlinesamount/2)*24*MenuScale),cl\txt,True
				EndIf
			EndIf
		Next
	EndIf
	
	If GetKey() Then CreditsTimer=-1
	
	If CreditsTimer=-1
		FreeFont I_Opt\Fonts[6]
		FreeFont I_Opt\Fonts[7]
		FreeImage CreditsScreen
		CreditsScreen = 0
		FreeImage EndingScreen
		EndingScreen = 0
		Delete Each CreditsLine
		NullGame(False)
		StopStream_Strict(MusicCHN)
		ShouldPlay = 21
		MenuOpen = False
		MainMenuOpen = True
		MainMenuTab = 0
		CurrSave = Null
		FlushKeys()
	EndIf
	
End Function


;--------------------------------------- player controls -------------------------------------------

Function SetCrouch(NewCrouch%)
	If Crouch <> NewCrouch Then PlaySound_Strict(CrouchSFX)
	Crouch = NewCrouch
End Function

Function MovePlayer()
	;CatchErrors("MovePlayer")
	
	Local Sprint# = 1.0, Speed# = 0.018, i%, angle#
	
	If I_Cheats\Speed Lor I_Cheats\SuperMan Then
		Speed = Speed * 3
	EndIf
	
	If I_Cheats\SuperMan Then	
		I_Cheats\SuperManTimer=I_Cheats\SuperManTimer+FPSfactor
		
		CameraShake = Sin(I_Cheats\SuperManTimer / 5.0) * (I_Cheats\SuperManTimer / 1500.0)
		
		If I_Cheats\SuperManTimer > 70 * 50 Then
			DeathMSG = GetLocalString("Deaths", "superman")
			Kill()
			ShowEntity Fog
		Else
			BlurTimer = 500		
			HideEntity Fog
		EndIf
	EndIf
	
	If DeathTimer > 0 Then
		DeathTimer=DeathTimer-FPSfactor
		If DeathTimer < 1 Then DeathTimer = -1.0
	ElseIf DeathTimer < 0 
		Kill()
	EndIf
	
	If CurrSpeed > 0 Then
		Stamina = Min(Stamina + 0.15 * FPSfactor/1.25, 100.0)
	Else
		Stamina = Min(Stamina + 0.15 * FPSfactor*1.25, 100.0)
	EndIf
	
	If StaminaEffectTimer > 0 Then
		StaminaEffectTimer = StaminaEffectTimer - (FPSfactor/70)
	Else
		If StaminaEffect <> 1.0 Then StaminaEffect = 1.0
	EndIf
	
	If PlayerRoom\RoomTemplate\Name<>"pocketdimension" Then 
		If KeyDown(I_Keys\SPRINT) Then
			If Stamina < 5 Then
				If ChannelPlaying(BreathCHN)=False Then BreathCHN = PlaySound_Strict(BreathSFX((WearingGasMask<>0 Lor Wearing1499<>0), 0))
			ElseIf Stamina < 50 And (Not I_Cheats\NoClip)
				If BreathRelaxedPlaying Then BreathRelaxedPlaying = 0 : StopChannel(BreathCHN) : BreathCHN = 0
				If BreathCHN=0 Lor (Not ChannelPlaying(BreathCHN)) Then
					BreathCHN = PlaySound_Strict(BreathSFX((WearingGasMask<>0 Lor Wearing1499<>0), Rand(1,3)))
					ChannelVolume BreathCHN, Min((70.0-Stamina)/70.0,1.0)*SFXVolume
					BreathRelaxedPlaying = False
				EndIf
			EndIf
		EndIf
	EndIf
	
	If (WearingGasMask<>0 Lor Wearing1499<>0) Then
		If (BreathCHN=0 Lor (Not ChannelPlaying(BreathCHN))) Then
			BreathCHN = PlaySound_Strict(BreathRelaxedSFX)
			ChannelVolume(BreathCHN, SFXVolume)
			BreathRelaxedPlaying = True
		EndIf
	ElseIf BreathRelaxedPlaying
		StopChannel(BreathCHN)
		BreathRelaxedPlaying = False
	EndIf
	
	For i = 0 To MaxItemAmount-1
		If Inventory(i)<>Null Then
			If Inventory(i)\itemtemplate\tempname = "finevest" Then Stamina = Min(Stamina, 60)
		EndIf
	Next
	
	If Wearing714 Then
		Stamina = Min(Stamina, 10)
		Sanity = Max(-850, Sanity)
	EndIf
	
	If IsZombie Then SetCrouch(False)
	
	If Abs(CrouchState-Crouch)<0.001 Then 
		CrouchState = Crouch
	Else
		CrouchState = CurveValue(Crouch, CrouchState, 10.0)
	EndIf
	
	If (Not I_Cheats\NoClip) Then 
		If (Playable And ((KeyDown(I_Keys\DOWN) Xor KeyDown(I_Keys\UP)) Lor (KeyDown(I_Keys\Right) Xor KeyDown(I_Keys\Left)))) Lor ForceMove>0 Then
			If Crouch = 0 And (KeyDown(I_Keys\SPRINT)) And Stamina > 0.0 And (Not IsZombie) Then
				Sprint = 2.5
				Stamina = Stamina - FPSfactor * 0.4 * StaminaEffect
				If Stamina <= 0 Then Stamina = -20.0
			EndIf
			
			If PlayerRoom\RoomTemplate\Name = "pocketdimension" Then
				If EntityY(Collider)<2000*RoomScale Lor EntityY(Collider)>2608*RoomScale Then
					Stamina = 0
					Speed = 0.015
					Sprint = 1.0					
				EndIf
			EndIf	
			
			If ForceMove>0 Then Speed=Speed*ForceMove
			
			If SelectedItem<>Null Then
				If SelectedItem\itemtemplate\tempname = "firstaid" Lor SelectedItem\itemtemplate\tempname = "finefirstaid" Lor SelectedItem\itemtemplate\tempname = "firstaid2" Then 
					Sprint = 0.0
				EndIf
			EndIf
			
			temp# = (Shake Mod 360)
			Local tempchn%
			If (Not UnableToMove%) Then Shake# = (Shake + FPSfactor * Min(Sprint, 1.5) * 7) Mod 720
			If temp < 180 And (Shake Mod 360) >= 180 And KillTimer>=0 Then
				If CurrStepSFX=0 Then
					temp = GetStepSound(Collider)
					
					If Sprint = 1.0 Then
						PlayerSoundVolume = Max(4.0,PlayerSoundVolume)
						tempchn% = PlaySound_Strict(StepSFX(GetStepSound(Collider), 0, Rand(0, 7)))
						ChannelVolume tempchn, (1.0-(Crouch*0.6))*SFXVolume#
					Else
						PlayerSoundVolume = Max(2.5-(Crouch*0.6),PlayerSoundVolume)
						tempchn% = PlaySound_Strict(StepSFX(GetStepSound(Collider), 1, Rand(0, 7)))
						ChannelVolume tempchn, (1.0-(Crouch*0.6))*SFXVolume#
					EndIf
				ElseIf CurrStepSFX=1
					tempchn% = PlaySound_Strict(Step2SFX(Rand(0, 2)))
					ChannelVolume tempchn, (1.0-(Crouch*0.4))*SFXVolume#
				ElseIf CurrStepSFX=2
					tempchn% = PlaySound_Strict(Step2SFX(Rand(3,5)))
					ChannelVolume tempchn, (1.0-(Crouch*0.4))*SFXVolume#
				ElseIf CurrStepSFX=3
					If Sprint = 1.0 Then
						PlayerSoundVolume = Max(4.0,PlayerSoundVolume)
						tempchn% = PlaySound_Strict(StepSFX(0, 0, Rand(0, 7)))
						ChannelVolume tempchn, (1.0-(Crouch*0.6))*SFXVolume#
					Else
						PlayerSoundVolume = Max(2.5-(Crouch*0.6),PlayerSoundVolume)
						tempchn% = PlaySound_Strict(StepSFX(0, 1, Rand(0, 7)))
						ChannelVolume tempchn, (1.0-(Crouch*0.6))*SFXVolume#
					EndIf
				EndIf
				
			EndIf	
		EndIf
	Else ;noclip on
		If (KeyDown(I_Keys\SPRINT)) Then
			Sprint = 2.5
		ElseIf KeyDown(I_Keys\CROUCH)
			Sprint = 0.5
		EndIf
	EndIf
	
	If KeyHit(I_Keys\CROUCH) And Playable And (Not IsZombie) And Bloodloss < 60.0 And I_427\Timer < 70.0 * 390.0 And (Not I_Cheats\NoClip) And (SelectedItem = Null Lor (SelectedItem\itemtemplate\tempname <> "firstaid" And SelectedItem\itemtemplate\tempname <> "finefirstaid" And SelectedItem\itemtemplate\tempname <> "firstaid2")) Then
		SetCrouch(Not Crouch)
	EndIf
	
	Local temp2# = (Speed * Sprint) / (1.0+CrouchState)
	
	If I_Cheats\NoClip Then 
		Shake = 0
		CurrSpeed = 0
		CrouchState = 0
		SetCrouch(False)
		
		RotateEntity Collider, WrapAngle(EntityPitch(Camera)), WrapAngle(EntityYaw(Camera)), 0
		
		temp2 = temp2 * I_Cheats\NoClipSpeed
		
		If KeyDown(I_Keys\DOWN) Then MoveEntity Collider, 0, 0, -temp2*FPSfactor
		If KeyDown(I_Keys\UP) Then MoveEntity Collider, 0, 0, temp2*FPSfactor
		
		If KeyDown(I_Keys\Left) Then MoveEntity Collider, -temp2*FPSfactor, 0, 0
		If KeyDown(I_Keys\Right) Then MoveEntity Collider, temp2*FPSfactor, 0, 0	
		
		ResetEntity Collider
	Else
		temp2# = temp2 / Max((Injuries+3.0)/3.0,1.0)
		If Injuries > 0.5 Then 
			temp2 = temp2*Min((Sin(Shake/2)+1.2),1.0)
		EndIf
		
		temp = False
		If (Not IsZombie%)
			If KeyDown(I_Keys\DOWN) And Playable Then
				If Not KeyDown(I_Keys\UP) Then
					temp = True
					angle = 180
					If KeyDown(I_Keys\Left) Then
						If Not KeyDown(I_Keys\Right) Then
							angle = 135
						EndIf
					ElseIf KeyDown(I_Keys\Right) Then
						angle = -135
					EndIf
				Else
					If KeyDown(I_Keys\Left) Then
						If Not KeyDown(I_Keys\Right) Then
							temp = True
							angle = 90
						EndIf
					ElseIf KeyDown(I_Keys\Right) Then
						temp = True
						angle = -90
					EndIf
				EndIf
			ElseIf KeyDown(I_Keys\UP) And Playable Then
				temp = True
				angle = 0
				If KeyDown(I_Keys\Left) Then
					If Not KeyDown(I_Keys\Right) Then
						angle = 45
					EndIf
				ElseIf KeyDown(I_Keys\Right) Then
					angle = -45 
				EndIf
			ElseIf ForceMove>0 Then
				temp=True
				angle = ForceAngle
			ElseIf Playable Then
				If KeyDown(I_Keys\Left) Then
					If Not KeyDown(I_Keys\Right) Then
						temp = True
						angle = 90
					EndIf
				ElseIf KeyDown(I_Keys\Right) Then
					temp = True
					angle = -90
				EndIf
			EndIf
		Else
			temp=True
			angle = ForceAngle
		EndIf
		
		angle = WrapAngle(EntityYaw(Collider,True)+angle+90.0)
		
		If temp Then 
			CurrSpeed = CurveValue(temp2, CurrSpeed, 20.0)
		Else
			CurrSpeed = Max(CurveValue(0.0, CurrSpeed-0.1, 1.0),0.0)
		EndIf
		
		If (Not UnableToMove%) Then TranslateEntity Collider, Cos(angle)*CurrSpeed * FPSfactor, 0, Sin(angle)*CurrSpeed * FPSfactor, True
		
		Local CollidedFloor% = False
		For i = 1 To CountCollisions(Collider)
			If CollisionY(Collider, i) < EntityY(Collider) - 0.25 Then CollidedFloor = True
		Next
		
		If CollidedFloor = True Then
			If DropSpeed# < - 0.07 Then 
				If CurrStepSFX=0 Then
					PlaySound_Strict(StepSFX(GetStepSound(Collider), 0, Rand(0, 7)))
				ElseIf CurrStepSFX=1
					PlaySound_Strict(Step2SFX(Rand(0, 2)))
				ElseIf CurrStepSFX=2
					PlaySound_Strict(Step2SFX(Rand(3, 5)))
				ElseIf CurrStepSFX=3
					PlaySound_Strict(StepSFX(0, 0, Rand(0, 7)))
				EndIf
				PlayerSoundVolume = Max(3.0,PlayerSoundVolume)
			EndIf
			DropSpeed# = 0
		Else
			;DropSpeed# = Min(Max(DropSpeed - 0.006 * FPSfactor, -2.0), 0.0)
			If PlayerFallingPickDistance#<>0.0
				Local pick = LinePick(EntityX(Collider),EntityY(Collider),EntityZ(Collider),0,-PlayerFallingPickDistance,0)
				If pick
					DropSpeed# = Min(Max(DropSpeed - 0.006 * FPSfactor, -2.0), 0.0)
				Else
					DropSpeed# = 0
				EndIf
			Else
				DropSpeed# = Min(Max(DropSpeed - 0.006 * FPSfactor, -2.0), 0.0)
			EndIf
		EndIf
		PlayerFallingPickDistance# = 10.0
		
		If (Not UnableToMove%) And ShouldEntitiesFall Then TranslateEntity Collider, 0, DropSpeed * FPSfactor, 0
	EndIf
	
	ForceMove = False
	
	If Injuries > 1.0 Then
		temp2 = Bloodloss
		BlurTimer = Max(Max(Sin(MilliSecs()/100.0)*Bloodloss*30.0,Bloodloss*2*(2.0-CrouchState)),BlurTimer)
		If (I_427\Using = 0 And I_427\Timer < 70*360) Then
			Bloodloss = Min(Bloodloss + (Min(Injuries,3.5)/300.0)*FPSfactor,100)
		EndIf
		
		If temp2 <= 60 And Bloodloss > 60 Then
			Msg = GetLocalString("Messages", "bloodfaint")
			MsgTimer = 70*4
		EndIf
	EndIf
	
	UpdateInfect()
	
	If Bloodloss > 0 Then
		If Rnd(200)<Min(Injuries,4.0) Then
			pvt = CreatePivot()
			PositionEntity pvt, EntityX(Collider)+Rnd(-0.05,0.05),EntityY(Collider)-0.05,EntityZ(Collider)+Rnd(-0.05,0.05)
			TurnEntity pvt, 90, 0, 0
			EntityPick(pvt,0.3)
			de.decals = CreateDecal(Rand(15,16), PickedX(), PickedY()+0.005, PickedZ(), 90, Rand(360), 0)
			de\size = Rnd(0.03,0.08)*Min(Injuries,3.0) : EntityAlpha(de\obj, 1.0) : ScaleSprite de\obj, de\size, de\size
			tempchn% = PlaySound_Strict (DripSFX(Rand(0,2)))
			ChannelVolume tempchn, Rnd(0.0,0.8)*SFXVolume
			ChannelPitch tempchn, Rand(20000,30000)
			
			FreeEntity pvt
		EndIf
		
		CurrCameraZoom = Max(CurrCameraZoom, (Sin(Float(MilliSecs())/20.0)+1.0)*Bloodloss*0.2)
		
		If Bloodloss > 60.0 Then
			SetCrouch(True)
		EndIf
		If Bloodloss => 100 Then 
			Kill()
			HeartBeatVolume = 0.0
		ElseIf Bloodloss > 80.0
			HeartBeatRate = Max(150-(Bloodloss-80)*5,HeartBeatRate)
			HeartBeatVolume = Max(HeartBeatVolume, 0.75+(Bloodloss-80.0)*0.0125)	
		ElseIf Bloodloss > 35.0
			HeartBeatRate = Max(70+Bloodloss,HeartBeatRate)
			HeartBeatVolume = Max(HeartBeatVolume, (Bloodloss-35.0)/60.0)			
		EndIf
	EndIf
	
	If HealTimer > 0 Then
		DebugLog HealTimer
		HealTimer = HealTimer - (FPSfactor / 70)
		Bloodloss = Min(Bloodloss + (2 / 400.0) * FPSfactor, 100)
		Injuries = Max(Injuries - (FPSfactor / 70) / 30, 0.0)
	EndIf
		
	If Playable Lor CanBlinkDespitePlayable Then
		If KeyHit(I_Keys\BLINK) Then BlinkTimer = 0
		If KeyDown(I_Keys\BLINK) And BlinkTimer < - 10 Then BlinkTimer = -10
	EndIf
	
	
	If HeartBeatVolume > 0 Then
		If HeartBeatTimer <= 0 Then
			tempchn = PlaySound_Strict (HeartBeatSFX)
			ChannelVolume tempchn, HeartBeatVolume*SFXVolume#
			
			HeartBeatTimer = 70.0*(60.0/Max(HeartBeatRate,1.0))
		Else
			HeartBeatTimer = HeartBeatTimer - FPSfactor
		EndIf
		
		HeartBeatVolume = Max(HeartBeatVolume - FPSfactor*0.05, 0)
	EndIf
	
	;CatchErrors("Uncaught MovePlayer")
End Function

Function MouseLook()
	Local i%
	
	CameraShake = Max(CameraShake - (FPSfactor / 10), 0)
	
	;CameraZoomTemp = CurveValue(CurrCameraZoom,CameraZoomTemp, 5.0)
	CameraZoom(Camera, Min(1.0+(CurrCameraZoom/400.0),1.1) / Tan((2*ATan(Tan(Float(FOV * (InvertCam*-2+1))/2)*RealGraphicWidth/RealGraphicHeight))/2.0))
	CurrCameraZoom = Max(CurrCameraZoom - FPSfactor, 0)
	
	If KillTimer >= 0 And FallTimer >=0 Then
		
		HeadDropSpeed = 0
		
		Local up# = (Sin(Shake) / (20.0+CrouchState*20.0))*0.6;, side# = Cos(Shake / 2.0) / 35.0		
		Local roll# = Max(Min(Sin(Shake/2)*2.5*Min(Injuries+0.25,3.0),8.0),-8.0)
		
		;käännetään kameraa sivulle jos pelaaja on vammautunut
		;RotateEntity Collider, EntityPitch(Collider), EntityYaw(Collider), Clamp(up*30*Injuries,-50,50)
		PositionEntity Camera, EntityX(Collider) + side, EntityY(Collider) + up + 0.6 + CrouchState * -0.3, EntityZ(Collider)
		RotateEntity Camera, 0, EntityYaw(Collider), roll*0.5
		
		;RotateEntity Collider, EntityPitch(Collider), EntityYaw(Collider), 0
		;moveentity player, side, up, 0	
		; -- Update the smoothing que To smooth the movement of the mouse.
		mouse_x_speed_1# = CurveValue(MouseXSpeed() * (MouseSens + 0.6) , mouse_x_speed_1, (6.0 / (MouseSens + 1.0))*MouseSmooth) 
		If IsNaN(mouse_x_speed_1) Then mouse_x_speed_1 = 0
		If PrevFPSFactor>0 Then
			If Abs(FPSfactor/PrevFPSFactor-1.0)>1.0 Then
				;lag spike detected - stop all camera movement
				mouse_x_speed_1 = 0.0
				mouse_y_speed_1 = 0.0
			EndIf
		EndIf
		If InvertMouse Then
			mouse_y_speed_1# = CurveValue(-MouseYSpeed() * (MouseSens + 0.6), mouse_y_speed_1, (6.0/(MouseSens+1.0))*MouseSmooth)
		Else
			mouse_y_speed_1# = CurveValue(MouseYSpeed () * (MouseSens + 0.6), mouse_y_speed_1, (6.0/(MouseSens+1.0))*MouseSmooth)
		EndIf
		If IsNaN(mouse_y_speed_1) Then mouse_y_speed_1 = 0
		
		Local the_yaw# = ((mouse_x_speed_1#)) * mouselook_x_inc# / (1.0+WearingVest+(WearingVest=-1)*0.6)
		Local the_pitch# = ((mouse_y_speed_1#)) * mouselook_y_inc# / (1.0+WearingVest+(WearingVest=-1)*0.6)
		
		TurnEntity Collider, 0.0, -the_yaw#, 0.0 ; Turn the user on the Y (yaw) axis.
		user_camera_pitch# = user_camera_pitch# + the_pitch#
		; -- Limit the user's camera To within 180 degrees of pitch rotation. ;EntityPitch(); returns useless values so we need To use a variable To keep track of the camera pitch.
		If user_camera_pitch# > 70.0 Then user_camera_pitch# = 70.0
		If user_camera_pitch# < - 70.0 Then user_camera_pitch# = -70.0
		
		RotateEntity Camera, WrapAngle(user_camera_pitch + Rnd(-CameraShake, CameraShake)), WrapAngle(EntityYaw(Collider) + Rnd(-CameraShake, CameraShake)), roll ; Pitch the user;s camera up And down.
		
		If PlayerRoom\RoomTemplate\Name = "pocketdimension" Then
			If EntityY(Collider)<2000*RoomScale Lor EntityY(Collider)>2608*RoomScale Then
				RotateEntity Camera, WrapAngle(EntityPitch(Camera)),WrapAngle(EntityYaw(Camera)), roll+WrapAngle(Sin(MilliSecs()/150.0)*30.0) ; Pitch the user;s camera up And down.
			EndIf
		EndIf
		
	Else
		HideEntity Collider
		PositionEntity Camera, EntityX(Head), EntityY(Head), EntityZ(Head)
		
		Local CollidedFloor% = False
		For i = 1 To CountCollisions(Head)
			If CollisionY(Head, i) < EntityY(Head) - 0.01 Then CollidedFloor = True
		Next
		
		If CollidedFloor = True Then
			HeadDropSpeed# = 0
		Else
			
			If KillAnim = 0 Then 
				MoveEntity Head, 0, 0, HeadDropSpeed
				RotateEntity(Head, CurveAngle(-90.0, EntityPitch(Head), 20.0), EntityYaw(Head), EntityRoll(Head))
				RotateEntity(Camera, CurveAngle(EntityPitch(Head) - 40.0, EntityPitch(Camera), 40.0), EntityYaw(Camera), EntityRoll(Camera))
			Else
				MoveEntity Head, 0, 0, -HeadDropSpeed
				RotateEntity(Head, CurveAngle(90.0, EntityPitch(Head), 20.0), EntityYaw(Head), EntityRoll(Head))
				RotateEntity(Camera, CurveAngle(EntityPitch(Head) + 40.0, EntityPitch(Camera), 40.0), EntityYaw(Camera), EntityRoll(Camera))
			EndIf
			
			HeadDropSpeed# = HeadDropSpeed - 0.002 * FPSfactor
		EndIf
		
		If InvertMouse Then
			TurnEntity (Camera, -MouseYSpeed() * 0.05 * FPSfactor, -MouseXSpeed() * 0.15 * FPSfactor, 0)
		Else
			TurnEntity (Camera, MouseYSpeed() * 0.05 * FPSfactor, -MouseXSpeed() * 0.15 * FPSfactor, 0)
		EndIf
		
	EndIf
	
	;pölyhiukkasia
	If ParticleAmount=2
		If Rand(35) = 1 Then
			Local pvt% = CreatePivot()
			PositionEntity(pvt, EntityX(Camera, True), EntityY(Camera, True), EntityZ(Camera, True))
			RotateEntity(pvt, 0, Rnd(360), 0)
			If Rand(2) = 1 Then
				MoveEntity(pvt, 0, Rnd(-0.5, 0.5), Rnd(0.5, 1.0))
			Else
				MoveEntity(pvt, 0, Rnd(-0.5, 0.5), Rnd(0.5, 1.0))
			EndIf
			
			Local p.Particles = CreateParticle(EntityX(pvt), EntityY(pvt), EntityZ(pvt), 2, 0.002, 0, 300)
			p\speed = 0.001
			RotateEntity(p\pvt, Rnd(-20, 20), Rnd(360), 0)
			
			p\SizeChange = -0.00001
			
			FreeEntity pvt
		EndIf
	EndIf
	
	; -- Limit the mouse's movement. Using this method produces smoother mouselook movement than centering the mouse Each loop.
	If (MouseX() > mouse_right_limit) Lor (MouseX() < mouse_left_limit) Lor (MouseY() > mouse_bottom_limit) Lor (MouseY() < mouse_top_limit)
		MoveMouse viewport_center_x, viewport_center_y
	EndIf
	
	If WearingGasMask Lor Wearing1499 Then
		If Wearing714 = False Then
			If WearingGasMask = 2 Lor Wearing1499 = 2 Then
				Stamina = Min(100, Stamina + (100.0-Stamina)*0.01*FPSfactor)
			ElseIf WearingGasMask = -1 Lor Wearing1499 = -1 Then
				Stamina = Max(0, Min(80, Stamina - 0.1*FPSfactor))
			EndIf
		EndIf
		
		ShowEntity(GasMaskOverlay)
	Else
		HideEntity(GasMaskOverlay)
	EndIf
	
	If WearingHazmat Then
		If Wearing714 = False Then
			If WearingHazmat = 2 Then
				Stamina = Min(100, Stamina + (100.0-Stamina)*0.01*FPSfactor)
			ElseIf WearingHazmat = -1
				Stamina = Max(0, Min(80, Stamina - 0.1*FPSfactor))
			EndIf
		EndIf
		If WearingHazmat = 1 Then
			Stamina = Min(60, Stamina)
		EndIf
		ShowEntity(HazmatOverlay)
	Else
		HideEntity(HazmatOverlay)
	EndIf
	
	If (WearingNightVision<>0 Lor WearingScramble<>0) Then
		ShowEntity(NVOverlay)
		If WearingNightVision=2 Then
			EntityColor(NVOverlay, 0,100,255)
			AmbientLightRooms(15)
		ElseIf WearingNightVision=3 Then
			EntityColor(NVOverlay, 255,0,0)
			AmbientLightRooms(15)
		ElseIf WearingNightVision=-1
			EntityColor(NVOverlay, 128,128,128)
		ElseIf WearingNightVision=1
			EntityColor(NVOverlay, 0,255,0)
			AmbientLightRooms(15)
		Else ;WearingScramble
			EntityColor(NVOverlay, 175,175,175)
		EndIf
		EntityTexture(Fog, FogNVTexture)
	Else
		AmbientLightRooms(0)
		HideEntity(NVOverlay)
		EntityTexture(Fog, FogTexture)
	EndIf
	
	If Wearing178<>0 Then
		ShouldPlay = 14
		ShowEntity(GlassesOverlay)
	Else
		HideEntity(GlassesOverlay)
	EndIf
	
	Local canSpawn178%=0
	
	If Wearing178=0 Then
		For n.NPCs = Each NPCs
			If (n\NPCtype = NPCtype178) Then
				If n\State3>0 Then canSpawn178=1
				If (n\State<=0) And (n\State3=0) Lor EntityDistanceSquared(Collider,n\Collider) > PowTwo(HideDistance*1.5) Then
					RemoveNPC(n)
				EndIf
			EndIf
		Next
	EndIf
	
	If (canSpawn178=1) Lor (Wearing178<>0) Then
		tempint%=0
		For n.NPCs = Each NPCs
			If (n\NPCtype = NPCtype178) Then
				tempint=tempint+1
				If EntityDistanceSquared(Collider,n\Collider) > PowTwo(HideDistance*1.5) Then
					RemoveNPC(n)
				EndIf
				;If n\State<=0 Then RemoveNPC(n)
			EndIf
		Next
		If tempint<10 Then ;create the npcs
			For w.WayPoints = Each WayPoints
				Local dist# = EntityDistanceSquared(Collider,w\obj)
				If (dist<PowTwo(HideDistance*1.5)) And (dist>PowTwo(1.2)) And (w\door = Null) And (Rand(Wearing178=2,1)=1) Then
					tempint2=True
					For n.NPCs = Each NPCs
						If n\NPCtype=NPCtype178 Then
							If EntityDistanceSquared(n\Collider,w\obj) < PowTwo(0.5) Then
								tempint2=False
								Exit
							EndIf
						EndIf
					Next
					If tempint2 Then
						CreateNPC(NPCtype178, EntityX(w\obj,True),EntityY(w\obj,True)+0.15,EntityZ(w\obj,True))
					EndIf	
				EndIf
			Next
		EndIf
	EndIf
	
	Local factor1025# = FPSfactor * SCP1025state[7]
	For i = 0 To 6
		If SCP1025state[i]>0 Then
			Select i
				Case 0 ;common cold
					If FPSfactor>0 Then 
						If Rand(1000)=1 Then
							If CoughCHN = 0 Then
								CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
							Else
								If Not ChannelPlaying(CoughCHN) Then CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
							EndIf
						EndIf
					EndIf
					Stamina = Stamina - factor1025 * 0.3
				Case 1 ;chicken pox
					If Rand(9000)=1 And Msg="" Then
						Msg=GetLocalString("Messages", "10251itchy")
						MsgTimer = 70*4
					EndIf
				Case 2 ;cancer of the lungs
					If factor1025>0 Then 
						If Rand(800)=1 Then
							If CoughCHN = 0 Then
								CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
							Else
								If Not ChannelPlaying(CoughCHN) Then CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
							EndIf
						EndIf
					EndIf
					Stamina = Stamina - factor1025 * 0.1
				Case 3 ;appendicitis
					;0.035/sec = 2.1/min
					If (I_427\Using = 0 And I_427\Timer < 70*360) Then
						SCP1025state[i]=SCP1025state[i]+factor1025*0.0005
					EndIf
					If SCP1025state[i]>20.0 Then
						If SCP1025state[i]-factor1025<=20.0 Then Msg=GetLocalString("Messages", "10253achebad") : MsgTimer = 70*4
						Stamina = Stamina - factor1025 * 0.3
					ElseIf SCP1025state[i]>10.0
						If SCP1025state[i]-factor1025<=10.0 Then Msg=GetLocalString("Messages", "10253ache") : MsgTimer = 70*4
					EndIf
				Case 4 ;asthma
					If Stamina < 35 Then
						If Rand(Int(140+Stamina*8))=1 Then
							If CoughCHN = 0 Then
								CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
							Else
								If Not ChannelPlaying(CoughCHN) Then CoughCHN = PlaySound_Strict(CoughSFX(Rand(0, 2)))
							EndIf
						EndIf
						CurrSpeed = CurveValue(0, CurrSpeed, 10+Stamina*15)
					EndIf
				Case 5;cardiac arrest
					If (I_427\Using = 0 And I_427\Timer < 70*360) Then
						SCP1025state[i]=SCP1025state[i]+factor1025*0.35
					EndIf
					;35/sec
					If SCP1025state[i]>110 Then
						HeartBeatRate=0
						BlurTimer = Max(BlurTimer, 500)
						If SCP1025state[i]>140 Then 
							DeathMSG = GetLocalString("Deaths", "1025")
							Kill()
						EndIf
					Else
						HeartBeatRate=Max(HeartBeatRate, 70+SCP1025state[i])
						HeartBeatVolume = 1.0
					EndIf
				Case 6;stamina disease
					If (I_427\Using = 0 And I_427\Timer < 70*360) Then
						SCP1025state[i]=SCP1025state[i]+0.00025*factor1025*(100/SCP1025state[i])
					EndIf
					Stamina = Min(100, Stamina + (90.0-Stamina)*SCP1025state[i]*factor1025*0.00008)
					If SCP1025state[i]>15 And SCP1025state[i]-FPSfactor<=15 Then
						Msg = GetLocalString("Messages", "1025breathe")
						MsgTimer = 70*4
					EndIf
			End Select 
		EndIf
	Next
	
	
End Function

;--------------------------------------- GUI, menu etc ------------------------------------------------

Const INVENTORY_GFX_SIZE% = 70
Const INVENTORY_GFX_SPACING% = 35

Const NAV_WIDTH% = 287
Const NAV_HEIGHT% = 256

Function DrawGUI()
	;CatchErrors("DrawGUI")
	
	Local temp%, x%, y%, z%, i%, yawvalue#, pitchvalue#
	Local x2#,y2#,z2#
	Local n%, xtemp, ytemp, strtemp$
	
	Local e.Events, it.Items
	
	If MenuOpen Lor ConsoleOpen Lor SelectedDoor <> Null Lor InvOpen Lor OtherOpen<>Null Lor EndingTimer < 0 Then
		ShowPointer()
		If PlayingSince <> 0 Then SavePlayTime()
	Else
		HidePointer()
		If PlayingSince = 0 Then PlayingSince = MilliSecs()
	EndIf 	
	
	If PlayerRoom\RoomTemplate\Name = "pocketdimension" Then
		For e.Events = Each Events
			If e\room = PlayerRoom Then
				If Float(e\EventStr)<1000.0 Then
					If e\EventState > 600 Then
						If BlinkTimer < -3 And BlinkTimer > -10 Then
							If e\img = 0 Then
								If BlinkTimer > -5 And Rand(30)=1 Then
									PlaySound_Strict DripSFX(0)
									If e\img = 0 Then e\img = LoadImage_Strict("GFX\npcs\106face.jpg")
								EndIf
							Else
								DrawImage e\img, I_Opt\GraphicWidth/2-Rand(390,310), I_Opt\GraphicHeight/2-Rand(290,310)
							EndIf
						Else
							If e\img <> 0 Then FreeImage e\img : e\img = 0
						EndIf
							
						Exit
					EndIf
				Else
					If BlinkTimer < -3 And BlinkTimer > -10 Then
						If e\img = 0 Then
							If BlinkTimer > -5 Then
								If e\img = 0 Then
									e\img = LoadImage_Strict("GFX\kneelmortal.pd")
									If (ChannelPlaying(e\SoundCHN)) Then
										StopChannel(e\SoundCHN)
									EndIf
									e\SoundCHN = PlaySound_Strict(e\Sound)
								EndIf
							EndIf
						Else
							DrawImage e\img, I_Opt\GraphicWidth/2-Rand(390,310), I_Opt\GraphicHeight/2-Rand(290,310)
						EndIf
					Else
						If e\img <> 0 Then FreeImage e\img : e\img = 0
						If BlinkTimer < -3 Then
							If (Not ChannelPlaying(e\SoundCHN)) Then
								e\SoundCHN = PlaySound_Strict(e\Sound)
							EndIf
						Else
							If (ChannelPlaying(e\SoundCHN)) Then
								StopChannel(e\SoundCHN)
							EndIf
						EndIf
					EndIf
					
					Exit
				EndIf
			EndIf
		Next
	EndIf
	
	If ClosestButton <> 0 And SelectedDoor = Null And InvOpen = False And MenuOpen = False And OtherOpen = Null Then
		If SelectedDifficulty\otherFactors <> EXTREME Then
			temp% = CreatePivot()
			PositionEntity temp, EntityX(Camera), EntityY(Camera), EntityZ(Camera)
			PointEntity temp, ClosestButton
			yawvalue# = WrapAngle(EntityYaw(Camera) - EntityYaw(temp))
			If yawvalue > 90 And yawvalue <= 180 Then yawvalue = 90
			If yawvalue > 180 And yawvalue < 270 Then yawvalue = 270
			pitchvalue# = WrapAngle(EntityPitch(Camera) - EntityPitch(temp))
			If pitchvalue > 90 And pitchvalue <= 180 Then pitchvalue = 90
			If pitchvalue > 180 And pitchvalue < 270 Then pitchvalue = 270
			
			FreeEntity (temp)
			
			DrawImage(HandIcon, I_Opt\GraphicWidth / 2 + Sin(yawvalue) * (I_Opt\GraphicWidth / 3) - 32, I_Opt\GraphicHeight / 2 - Sin(pitchvalue) * (I_Opt\GraphicHeight / 3) - 32)
		EndIf
		
		If MouseUp1 Then
			MouseUp1 = False
			If ClosestDoor <> Null Then 
				If ClosestDoor\Code <> "" Then
					SelectedDoor = ClosestDoor
				ElseIf Playable Then
					PlaySound2(ButtonSFX, Camera, ClosestButton)
					UseDoor(ClosestDoor,True)				
				EndIf
			EndIf
		EndIf
	EndIf
	
	If ClosestItem <> Null And SelectedDifficulty\otherFactors <> EXTREME Then
		yawvalue# = -DeltaYaw(Camera, ClosestItem\collider)
		If yawvalue > 90 And yawvalue <= 180 Then yawvalue = 90
		If yawvalue > 180 And yawvalue < 270 Then yawvalue = 270
		pitchvalue# = -DeltaPitch(Camera, ClosestItem\collider)
		If pitchvalue > 90 And pitchvalue <= 180 Then pitchvalue = 90
		If pitchvalue > 180 And pitchvalue < 270 Then pitchvalue = 270
		DrawImage(HandIcon2, I_Opt\GraphicWidth / 2 + Sin(yawvalue) * (I_Opt\GraphicWidth / 3) - 32, I_Opt\GraphicHeight / 2 - Sin(pitchvalue) * (I_Opt\GraphicHeight / 3) - 32)
	EndIf
	
	If (DrawHandIcon And SelectedDifficulty\otherFactors <> EXTREME) Then DrawImage(HandIcon, I_Opt\GraphicWidth / 2 - 32, I_Opt\GraphicHeight / 2 - 32)
	For i = 0 To 3
		If DrawArrowIcon[i] Then
			x = I_Opt\GraphicWidth / 2 - 32
			y = I_Opt\GraphicHeight / 2 - 32		
			Select i
				Case 0
					y = y - 64 - 5
				Case 1
					x = x + 64 + 5
				Case 2
					y = y + 64 + 5
				Case 3
					x = x - 5 - 64
			End Select
			DrawImage(HandIcon, x, y)
			Color 0, 0, 0
			Rect(x + 4, y + 4, 64 - 8, 64 - 8)
			DrawImage(ArrowIMG[i], x + 21, y + 21)
			DrawArrowIcon[i] = False
		EndIf
	Next
	
	If Using294 Then Use294()
	
	If HUDenabled And SelectedDifficulty\otherFactors <> EXTREME Then 
		
		;width = 204
		;height = 20
		x% = 80
		y% = I_Opt\GraphicHeight - 95
		
		Color 255, 255, 255	
		Rect (x, y, 204, 20, False)
		For i = 1 To Int(((204 - 2) * (BlinkTimer / (BLINKFREQ))) / 10)
			DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
		Next	
		Color 0, 0, 0
		Rect(x - 50, y, 30, 30)
		
		If EyeIrritation > 0 Then
			Color 200, 0, 0
			Rect(x - 50 - 3, y - 3, 30 + 6, 30 + 6)
		EndIf
		
		Color 255, 255, 255
		Rect(x - 50 - 1, y - 1, 30 + 2, 30 + 2, False)
		
		DrawImage BlinkIcon, x - 50, y
		
		y = I_Opt\GraphicHeight - 55
		Color 255, 255, 255
		Rect (x, y, 204, 20, False)
		For i = 1 To Int(((204 - 2) * (Stamina / 100.0)) / 10)
			DrawImage(StaminaMeterIMG, x + 3 + 10 * (i - 1), y + 3)
		Next	
		
		Color 0, 0, 0
		Rect(x - 50, y, 30, 30)
		
		Color 255, 255, 255
		Rect(x - 50 - 1, y - 1, 30 + 2, 30 + 2, False)
		If Crouch Then
			DrawImage(CrouchIcon, x - 50, y)
		ElseIf KeyDown(I_Keys\SPRINT) And CurrSpeed > 0.0 And Stamina > 0.0 Then
			DrawImage(SprintIcon, x - 50, y)
		Else
			DrawImage(WalkIcon, x - 50, y)
		EndIf
		
		If DebugHUD Then
			Color 255, 255, 255
			SetFont I_Opt\Fonts[0]
			
			;Text x + 250, 50, "Zone: " + (EntityZ(Collider)/8.0)
			Text x - 50, 50, "Player Position: (" + f2s(EntityX(Collider), 3) + ", " + f2s(EntityY(Collider), 3) + ", " + f2s(EntityZ(Collider), 3) + ")"
			Text x - 50, 70, "Camera Position: (" + f2s(EntityX(Camera), 3)+ ", " + f2s(EntityY(Camera), 3) +", " + f2s(EntityZ(Camera), 3) + ")"
			Text x - 50, 100, "Player Rotation: (" + f2s(EntityPitch(Collider), 3) + ", " + f2s(EntityYaw(Collider), 3) + ", " + f2s(EntityRoll(Collider), 3) + ")"
			Text x - 50, 120, "Camera Rotation: (" + f2s(EntityPitch(Camera), 3)+ ", " + f2s(EntityYaw(Camera), 3) +", " + f2s(EntityRoll(Camera), 3) + ")"
			Text x - 50, 150, "Room: " + PlayerRoom\RoomTemplate\Name
			For ev.Events = Each Events
				If ev\room = PlayerRoom Then
					Text x - 50, 170, "Room event: " + ev\EventName   
					Text x - 50, 190, "state: " + ev\EventState
					Text x - 50, 210, "state2: " + ev\EventState2   
					Text x - 50, 230, "state3: " + ev\EventState3
					Text x - 50, 250, "str: "+ ev\EventStr
					Exit
				EndIf
			Next
			Text x - 50, 280, "Room coordinates: (" + Floor(EntityX(PlayerRoom\obj) / 8.0 + 0.5) + ", " + Floor(EntityZ(PlayerRoom\obj) / 8.0 + 0.5) + ", angle: "+PlayerRoom\angle + ")"
			Text x - 50, 300, "Stamina: " + f2s(Stamina, 3)
			Text x - 50, 320, "Death timer: " + f2s(KillTimer, 3)			   
			Text x - 50, 340, "Blink timer: " + f2s(BlinkTimer, 3)
			Text x - 50, 360, "Injuries: " + Injuries
			Text x - 50, 380, "Bloodloss: " + Bloodloss
			If Curr173 <> Null
				Text x - 50, 410, "SCP - 173 Position (collider): (" + f2s(EntityX(Curr173\Collider), 3) + ", " + f2s(EntityY(Curr173\Collider), 3) + ", " + f2s(EntityZ(Curr173\Collider), 3) + ")"
				Text x - 50, 430, "SCP - 173 Position (obj): (" + f2s(EntityX(Curr173\obj), 3) + ", " + f2s(EntityY(Curr173\obj), 3) + ", " + f2s(EntityZ(Curr173\obj), 3) + ")"
				;Text x - 50, 410, "SCP - 173 Idle: " + Curr173\Idle
				Text x - 50, 450, "SCP - 173 State: " + Curr173\State
			EndIf
			If Curr106 <> Null
				Text x - 50, 470, "SCP - 106 Position (collider): (" + f2s(EntityX(Curr106\Collider), 3) + ", " + f2s(EntityY(Curr106\Collider), 3) + ", " + f2s(EntityZ(Curr106\Collider), 3) + ")"
				Text x - 50, 490, "SCP - 106 Position (obj): (" + f2s(EntityX(Curr106\obj), 3) + ", " + f2s(EntityY(Curr106\obj), 3) + ", " + f2s(EntityZ(Curr106\obj), 3) + ")"
				Text x - 50, 510, "SCP - 106 Idle: " + Curr106\Idle
				Text x - 50, 530, "SCP - 106 State: " + Curr106\State
			EndIf
			offset% = 0
			For npc.NPCs = Each NPCs
				If npc\NPCtype = NPCtype096 Then
					Text x - 50, 550, "SCP - 096 Position: (" + f2s(EntityX(npc\obj), 3) + ", " + f2s(EntityY(npc\obj), 3) + ", " + f2s(EntityZ(npc\obj), 3) + ")"
					Text x - 50, 570, "SCP - 096 Idle: " + npc\Idle
					Text x - 50, 590, "SCP - 096 State: " + npc\State
					Text x - 50, 610, "SCP - 096 Speed: " + f2s(npc\currspeed, 5)
				EndIf
				If npc\NPCtype = NPCtypeMTF Then
					Text x - 50, 640 + 60 * offset, "MTF " + offset + " Position: (" + f2s(EntityX(npc\obj), 3) + ", " + f2s(EntityY(npc\obj), 3) + ", " + f2s(EntityZ(npc\obj), 3) + ")"
					Text x - 50, 660 + 60 * offset, "MTF " + offset + " State: " + npc\State
					Text x - 50, 680 + 60 * offset, "MTF " + offset + " LastSeen: " + npc\lastseen					
					offset = offset + 1
				EndIf
			Next
			If PlayerRoom\RoomTemplate\Name$ = "dimension1499"
				Text x + 350, 50, "Current Chunk X/Z: ("+(Int((EntityX(Collider)+20)/40))+", "+(Int((EntityZ(Collider)+20)/40))+")"
				Local CH_Amount% = 0
				For ch.Chunk = Each Chunk
					CH_Amount = CH_Amount + 1
				Next
				Text x + 350, 70, "Current Chunk Amount: "+CH_Amount
			Else
				Text x + 350, 50, "Current Room Position: ("+PlayerRoom\x+", "+PlayerRoom\y+", "+PlayerRoom\z+")"
			EndIf
			Text x + 350, 90, ((TotalPhys()/1024)-(AvailPhys()/1024))+" MB/"+(TotalPhys()/1024)+" MB"
			Text x + 350, 110, "Triangles rendered: "+CurrTrisAmount
			Text x + 350, 130, "Active textures: "+ActiveTextures()
			Text x + 350, 150, "SCP-427 state (secs): "+Int(I_427\Timer/70.0)
			Text x + 350, 170, "SCP-008 infection: "+Infect
			For i = 0 To 7
				Text x + 350, 190+(20*i), "SCP-1025 State "+i+": "+SCP1025state[i]
			Next
			If SelectedMonitor <> Null Then
				Text x + 350, 350, "Current monitor: "+SelectedMonitor\ScrObj
			Else
				Text x + 350, 350, "Current monitor: NULL"
			EndIf
			Text x + 350, 370, "Current Zone: " + CurrentZone
			Text x + 350, 390, "CurrentTrigger: " + CheckTriggers()
			
			SetFont I_Opt\Fonts[1]
		EndIf
		
	EndIf
	
	If SelectedScreen <> Null Then
		DrawImage SelectedScreen\img, I_Opt\GraphicWidth/2-ImageWidth(SelectedScreen\img)/2,I_Opt\GraphicHeight/2-ImageHeight(SelectedScreen\img)/2
		
		If MouseUp1 Lor MouseHit2 Then
			FreeImage SelectedScreen\img : SelectedScreen\img = 0
			SelectedScreen = Null
			MouseUp1 = False
		EndIf
	EndIf
	
	Local PrevInvOpen% = InvOpen, MouseSlot% = 66
	
	Local shouldDrawHUD%=True
	If SelectedDoor <> Null Then
		SelectedItem = Null
		
		If shouldDrawHUD Then
			CameraZoom(Camera, Min(1.0+(CurrCameraZoom/400.0),1.1) / Tan((2*ATan(Tan((74)/2)*RealGraphicWidth/RealGraphicHeight))/2.0))
			pvt = CreatePivot()
			PositionEntity pvt, EntityX(ClosestButton,True),EntityY(ClosestButton,True),EntityZ(ClosestButton,True)
			RotateEntity pvt, 0, EntityYaw(ClosestButton,True)-180,0
			MoveEntity pvt, 0,0,0.22
			PositionEntity Camera, EntityX(pvt),EntityY(pvt),EntityZ(pvt)
			PointEntity Camera, ClosestButton
			FreeEntity pvt	
			
			CameraProject(Camera, EntityX(ClosestButton,True),EntityY(ClosestButton,True)+MeshHeight(ButtonOBJ)*0.015,EntityZ(ClosestButton,True))
			projY# = ProjectedY()
			CameraProject(Camera, EntityX(ClosestButton,True),EntityY(ClosestButton,True)-MeshHeight(ButtonOBJ)*0.015,EntityZ(ClosestButton,True))
			scale# = (ProjectedY()-projy)/462.0
			
			x = I_Opt\GraphicWidth/2-ImageWidth(KeypadHUD)*scale/2
			y = I_Opt\GraphicHeight/2-ImageHeight(KeypadHUD)*scale/2		
			
			SetFont I_Opt\Fonts[3]
			If KeypadMSG <> "" Then 
				KeypadTimer = KeypadTimer-FPSfactor2
				
				If (KeypadTimer Mod 70) < 35 Then Text I_Opt\GraphicWidth/2, y+124*scale, KeypadMSG, True,True
				If KeypadTimer =<0 Then
					KeypadMSG = ""
					SelectedDoor = Null
					MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
				EndIf
			Else
				Text I_Opt\GraphicWidth/2, y+70*scale, "ACCESS CODE: ",True,True	
				SetFont I_Opt\Fonts[4]
				Text I_Opt\GraphicWidth/2, y+124*scale, KeypadInput,True,True	
			EndIf
			
			x = x+44*scale
			y = y+249*scale
			
			For n = 0 To 3
				For i = 0 To 2
					xtemp = x+Int(58.5*scale*n)
					ytemp = y+(67*scale)*i
					
					temp = False
					If MouseOn(xtemp,ytemp, 54*scale,65*scale) And KeypadMSG = "" Then
						If MouseUp1 Then 
							PlaySound_Strict ButtonSFX
							
							Select (n+1)+(i*4)
								Case 1,2,3
									KeypadInput=KeypadInput + ((n+1)+(i*4))
								Case 4
									KeypadInput=KeypadInput + "0"
								Case 5,6,7
									KeypadInput=KeypadInput + ((n+1)+(i*4)-1)
								Case 8 ;enter
									If KeypadInput = SelectedDoor\Code Then
										PlaySound_Strict ScannerSFX1
										
										If SelectedDoor\Code = Str(AccessCode) Then
											GiveAchievement(AchvMaynard)
										ElseIf SelectedDoor\Code = "7816"
											GiveAchievement(AchvHarp)
										EndIf									
										
										SelectedDoor\locked = 0
										UseDoor(SelectedDoor,True)
										SelectedDoor = Null
										MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
									Else
										PlaySound_Strict ScannerSFX2
										KeypadMSG = "ACCESS DENIED"
										KeypadTimer = 210
										KeypadInput = ""	
									EndIf
								Case 9,10,11
									KeypadInput=KeypadInput + ((n+1)+(i*4)-2)
								Case 12
									KeypadInput = ""
							End Select 
							
							If Len(KeypadInput)> 4 Then KeypadInput = Left(KeypadInput,4)
						EndIf
						
					Else
						temp = False
					EndIf
					
				Next
			Next
			
			If I_Opt\GraphicMode = 0 Then DrawImage CursorIMG, ScaledMouseX(),ScaledMouseY()
			
			If MouseHit2 Then
				SelectedDoor = Null
				MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
			EndIf
		Else
			SelectedDoor = Null
		EndIf
	Else
		KeypadInput = ""
		KeypadTimer = 0
		KeypadMSG = ""
	EndIf
	
	If KeyHit(1) And EndingTimer=0 And (Not Using294) Then
		If MenuOpen Lor InvOpen Then
			ResumeSounds()
			If OptionsMenu <> 0 Then SaveOptionsINI()
			MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
		Else
			PauseSounds()
		EndIf
		MenuOpen = (Not MenuOpen)
		
		AchievementsMenu = 0
		OptionsMenu = 0
		QuitMSG = 0
		
		SelectedDoor = Null
		SelectedScreen = Null
		SelectedMonitor = Null
		If SelectedItem <> Null Then
			If Instr(SelectedItem\itemtemplate\tempname,"vest") Lor Instr(SelectedItem\itemtemplate\tempname,"hazmat") Then
				If (WearingVest = 0) And (WearingHazmat = 0) Then
					DropItem(SelectedItem)
				EndIf
				SelectedItem = Null
			EndIf
		EndIf
	EndIf
	
	Local PrevOtherOpen.Items
	
	Local OtherSize%,OtherAmount%
	
	Local isEmpty%
	
	Local isMouseOn%
	
	Local closedInv%
	
	If OtherOpen<>Null Then

		If (PlayerRoom\RoomTemplate\Name = "gatea") Then
			HideEntity Fog
			CameraFogRange Camera, 5,30
			CameraFogColor (Camera,200,200,200)
			CameraClsColor (Camera,200,200,200)					
			CameraRange(Camera, 0.05, 30)
		ElseIf (PlayerRoom\RoomTemplate\Name = "gateb") And (EntityY(Collider)>1040.0*RoomScale)
			HideEntity Fog
			CameraFogRange Camera, 5,45
			CameraFogColor (Camera,200,200,200)
			CameraClsColor (Camera,200,200,200)					
			CameraRange(Camera, 0.05, 60)
		EndIf
		
		PrevOtherOpen = OtherOpen
		OtherSize=OtherOpen\invSlots;Int(OtherOpen\state2)
		
		For i%=0 To OtherSize-1
			If OtherOpen\SecondInv[i] <> Null Then
				OtherAmount = OtherAmount+1
			EndIf
		Next
		
		;If OtherAmount > 0 Then
		;	OtherOpen\state = 1.0
		;Else
		;	OtherOpen\state = 0.0
		;EndIf
		InvOpen = False
		SelectedDoor = Null
		Local tempX% = 0
		
		x = I_Opt\GraphicWidth / 2 - (INVENTORY_GFX_SIZE * 10 /2 + INVENTORY_GFX_SPACING * (10 / 2 - 1)) / 2
		y = I_Opt\GraphicHeight / 2 - INVENTORY_GFX_SIZE * (Float(OtherSize) / 10 * 2 - 1) - INVENTORY_GFX_SPACING
		
		ItemAmount = 0
		isMouseOn = -1
		For  n% = 0 To OtherSize - 1
			If ScaledMouseX() > x And ScaledMouseX() < x + INVENTORY_GFX_SIZE Then
				If ScaledMouseY() > y And ScaledMouseY() < y + INVENTORY_GFX_SIZE Then
					isMouseOn = n
				EndIf
			EndIf
			
			If isMouseOn = n Then
				MouseSlot = n
				Color 255, 0, 0
				Rect(x - 1, y - 1, INVENTORY_GFX_SIZE + 2, INVENTORY_GFX_SIZE + 2)
			EndIf
			
			DrawFrame(x, y, INVENTORY_GFX_SIZE, INVENTORY_GFX_SIZE, (x Mod 64), (x Mod 64))
			
			If OtherOpen = Null Then Exit
			
			If OtherOpen\SecondInv[n] <> Null Then
				If (isMouseOn = n Lor SelectedItem <> OtherOpen\SecondInv[n]) Then
					DrawImage(OtherOpen\SecondInv[n]\invimg, x + INVENTORY_GFX_SIZE / 2 - 32, y + INVENTORY_GFX_SIZE / 2 - 32)
				EndIf
			EndIf
			DebugLog "otheropen: "+(OtherOpen<>Null)
			If OtherOpen\SecondInv[n] <> Null And SelectedItem <> OtherOpen\SecondInv[n] Then
				If isMouseOn = n Then
					Color 255, 255, 255	
					Text(x + INVENTORY_GFX_SIZE / 2, y + INVENTORY_GFX_SIZE + INVENTORY_GFX_SPACING - 15, OtherOpen\SecondInv[n]\itemtemplate\localname, True)				
					If SelectedItem = Null Then
						If MouseHit1 Then
							SelectedItem = OtherOpen\SecondInv[n]
							
							If DoubleClick And DoubleClickSlot = n Then
								If OtherOpen\SecondInv[n]\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(OtherOpen\SecondInv[n]\itemtemplate\sound))
								OtherOpen = Null
								closedInv = True
								InvOpen = False
								DoubleClick = False
							EndIf
						EndIf
					EndIf
				EndIf
				
				ItemAmount=ItemAmount+1
			Else
				If isMouseOn = n And MouseHit1 Then
					For z% = 0 To OtherSize - 1
						If OtherOpen\SecondInv[z] = SelectedItem Then OtherOpen\SecondInv[z] = Null
					Next
					OtherOpen\SecondInv[n] = SelectedItem
				EndIf
			EndIf					
			
			x=x+INVENTORY_GFX_SIZE + INVENTORY_GFX_SPACING
			tempX=tempX + 1
			If tempX = 5 Then 
				tempX=0
				y = y + INVENTORY_GFX_SIZE*2 
				x = I_Opt\GraphicWidth / 2 - (INVENTORY_GFX_SIZE * 10 /2 + INVENTORY_GFX_SPACING * (10 / 2 - 1)) / 2
			EndIf
		Next
		
		If MouseHit1 Then
			DoubleClickSlot = isMouseOn
		EndIf
		
		If SelectedItem <> Null Then
			If MouseDown1 Then
				If MouseSlot = 66 Then
					DrawImage(SelectedItem\invimg, ScaledMouseX() - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, ScaledMouseY() - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
				ElseIf SelectedItem <> PrevOtherOpen\SecondInv[MouseSlot]
					DrawImage(SelectedItem\invimg, ScaledMouseX() - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, ScaledMouseY() - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
				EndIf
			Else
				If MouseSlot = 66 Then
					If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
					
					ShowEntity(SelectedItem\collider)
					PositionEntity(SelectedItem\collider, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
					RotateEntity(SelectedItem\collider, EntityPitch(Camera), EntityYaw(Camera), 0)
					MoveEntity(SelectedItem\collider, 0, -0.1, 0.1)
					RotateEntity(SelectedItem\collider, 0, Rand(360), 0)
					ResetEntity(SelectedItem\collider)
					;move the item so that it doesn't overlap with other items
					;For it.Items = Each Items
					;	If it <> SelectedItem And it\Picked = False Then
					;		x = Abs(EntityX(SelectedItem\collider, True)-EntityX(it\collider, True))
					;		If x < 0.2 Then 
					;			z = Abs(EntityZ(SelectedItem\collider, True)-EntityZ(it\collider, True))
					;			If z < 0.2 Then
					;				While (x+z)<0.25
					;					MoveEntity(SelectedItem\collider, 0, 0, 0.025)
					;					x = Abs(EntityX(SelectedItem\collider, True)-EntityX(it\collider, True))
					;					z = Abs(EntityZ(SelectedItem\collider, True)-EntityZ(it\collider, True))
					;				Wend
					;			EndIf
					;		EndIf
					;	EndIf
					;Next
					
					SelectedItem\DropSpeed = 0.0
					
					SelectedItem\Picked = 0
					For z% = 0 To OtherSize - 1
						If OtherOpen\SecondInv[z] = SelectedItem Then OtherOpen\SecondInv[z] = Null
					Next
					
					isEmpty=True
					If OtherOpen\itemtemplate\tempname = "wallet" Then
						If (Not isEmpty) Then
							For z% = 0 To OtherSize - 1
								If OtherOpen\SecondInv[z]<>Null
									Local name$=OtherOpen\SecondInv[z]\itemtemplate\tempname
									If name$<>"quarter" And name$<>"coin" And name$<>"key" And name$<>"scp860" And name$<>"scp714" Then
										isEmpty=False
										Exit
									EndIf
								EndIf
							Next
						EndIf
					Else
						For z% = 0 To OtherSize - 1
							If OtherOpen\SecondInv[z]<>Null
								isEmpty = False
								Exit
							EndIf
						Next
					EndIf
					
					If isEmpty Then
						Select OtherOpen\itemtemplate\tempname
							Case "clipboard"
								OtherOpen\invimg = OtherOpen\itemtemplate\invimg2
								SetAnimTime OtherOpen\model,17.0
							Case "wallet"
								SetAnimTime OtherOpen\model,0.0
						End Select
					EndIf
					
					SelectedItem = Null
					OtherOpen = Null
					closedInv=True
					
					MoveMouse viewport_center_x, viewport_center_y
				Else
					
					If PrevOtherOpen\SecondInv[MouseSlot] = Null Then
						For z% = 0 To OtherSize - 1
							If PrevOtherOpen\SecondInv[z] = SelectedItem Then PrevOtherOpen\SecondInv[z] = Null
						Next
						PrevOtherOpen\SecondInv[MouseSlot] = SelectedItem
						SelectedItem = Null
					ElseIf PrevOtherOpen\SecondInv[MouseSlot] <> SelectedItem
						Select SelectedItem\itemtemplate\tempname
							Default
								Msg = GetLocalString("Messages", "cantcombine")
								MsgTimer = 70 * 5
						End Select					
					EndIf
					
				EndIf
				SelectedItem = Null
			EndIf
		EndIf
		
		If I_Opt\GraphicMode = 0 Then DrawImage CursorIMG,ScaledMouseX(),ScaledMouseY()
		If (closedInv) And (Not InvOpen) Then 
			ResumeSounds() 
			OtherOpen=Null
			MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
		EndIf

		
	ElseIf InvOpen Then
		
		If (PlayerRoom\RoomTemplate\Name = "gatea") Then
			HideEntity Fog
			CameraFogRange Camera, 5,30
			CameraFogColor (Camera,200,200,200)
			CameraClsColor (Camera,200,200,200)					
			CameraRange(Camera, 0.05, 30)
		ElseIf (PlayerRoom\RoomTemplate\Name = "gateb") And (EntityY(Collider)>1040.0*RoomScale)
			HideEntity Fog
			CameraFogRange Camera, 5,45
			CameraFogColor (Camera,200,200,200)
			CameraClsColor (Camera,200,200,200)					
			CameraRange(Camera, 0.05, 60)
		EndIf
		
		SelectedDoor = Null
		
		x = I_Opt\GraphicWidth / 2 - (INVENTORY_GFX_SIZE * MaxItemAmount /2 + INVENTORY_GFX_SPACING * (MaxItemAmount / 2 - 1)) / 2
		y = I_Opt\GraphicHeight / 2 - INVENTORY_GFX_SIZE - INVENTORY_GFX_SPACING
		
		If MaxItemAmount < 3 Then
			y = y + INVENTORY_GFX_SIZE
			x = x - (INVENTORY_GFX_SIZE * MaxItemAmount /2 + INVENTORY_GFX_SPACING) / 2
		EndIf
		
		ItemAmount = 0
		isMouseOn = -1
		For  n% = 0 To MaxItemAmount - 1
			If ScaledMouseX() > x And ScaledMouseX() < x + INVENTORY_GFX_SIZE Then
				If ScaledMouseY() > y And ScaledMouseY() < y + INVENTORY_GFX_SIZE Then
					isMouseOn = n
				EndIf
			EndIf
			
			If Inventory(n) <> Null Then
				Color 200, 200, 200
				If Inventory(n)\Picked = 2 Then
					Rect(x - 3, y - 3, INVENTORY_GFX_SIZE + 6, INVENTORY_GFX_SIZE + 6)
				EndIf
			EndIf
			
			If isMouseOn = n Then
				MouseSlot = n
				Color 255, 0, 0
				Rect(x - 1, y - 1, INVENTORY_GFX_SIZE + 2, INVENTORY_GFX_SIZE + 2)
			EndIf
			
			Color 255, 255, 255
			DrawFrame(x, y, INVENTORY_GFX_SIZE, INVENTORY_GFX_SIZE, (x Mod 64), (x Mod 64))
			
			If Inventory(n) <> Null Then
				If (isMouseOn = n Lor SelectedItem <> Inventory(n)) Then 
					DrawImage(Inventory(n)\invimg, x + INVENTORY_GFX_SIZE / 2 - 32, y + INVENTORY_GFX_SIZE / 2 - 32)
				EndIf
			EndIf
			
			If Inventory(n) <> Null And SelectedItem <> Inventory(n) Then
				;drawimage(Inventory(n).InvIMG, x + INVENTORY_GFX_SIZE / 2 - 32, y + INVENTORY_GFX_SIZE / 2 - 32)
				If isMouseOn = n Then
					If SelectedItem = Null Then
						If MouseHit1 Then
							SelectedItem = Inventory(n)
							
							If DoubleClick And DoubleClickSlot = n Then
								If WearingHazmat <> 0 And Instr(SelectedItem\itemtemplate\tempname,"hazmat")=0 Then
									Msg = GetLocalString("Messages", "canthazmat")
									MsgTimer = 70*5
									SelectedItem = Null
									Return
								EndIf
								If Inventory(n)\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(Inventory(n)\itemtemplate\sound))
								InvOpen = False
								DoubleClick = False
							EndIf
							
						EndIf
						
						SetFont I_Opt\Fonts[1]
						Color 0,0,0
						Text(x + INVENTORY_GFX_SIZE / 2 + 1, y + INVENTORY_GFX_SIZE + INVENTORY_GFX_SPACING - 15 + 1, Inventory(n)\localname, True)							
						Color 255, 255, 255	
						Text(x + INVENTORY_GFX_SIZE / 2, y + INVENTORY_GFX_SIZE + INVENTORY_GFX_SPACING - 15, Inventory(n)\localname, True)	
						
					EndIf
				EndIf
				
				ItemAmount=ItemAmount+1
			Else
				If isMouseOn = n And MouseHit1 Then
					For z% = 0 To MaxItemAmount - 1
						If Inventory(z) = SelectedItem Then Inventory(z) = Null
					Next
					Inventory(n) = SelectedItem
				EndIf
				
			EndIf					
			
			x=x+INVENTORY_GFX_SIZE + INVENTORY_GFX_SPACING
			If MaxItemAmount > 3 And n = MaxItemAmount/2 - 1 Then 
				y = y + INVENTORY_GFX_SIZE*2 
				x = I_Opt\GraphicWidth / 2 - (INVENTORY_GFX_SIZE * MaxItemAmount /2 + INVENTORY_GFX_SPACING * (MaxItemAmount / 2 - 1)) / 2
			EndIf
		Next
		
		If MouseHit1 Then
			DoubleClickSlot = isMouseOn
		EndIf
		
		If SelectedItem <> Null Then
			If MouseDown1 Then
				If MouseSlot = 66 Then
					DrawImage(SelectedItem\invimg, ScaledMouseX() - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, ScaledMouseY() - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
				ElseIf SelectedItem <> Inventory(MouseSlot)
					DrawImage(SelectedItem\invimg, ScaledMouseX() - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, ScaledMouseY() - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
				EndIf
			Else
				If MouseSlot = 66 Then
					Select SelectedItem\itemtemplate\tempname
						Case "badvest","vest","finevest","hazmat0","hazmat","hazmat2","hazmat3"
							Msg = GetLocalString("Messages", "doubleclickoff")
							MsgTimer = 70*5
						Case "bad1499","scp1499","super1499","fine1499"
							If Wearing1499<>0 Then
								Msg = GetLocalString("Messages", "doubleclickoff")
								MsgTimer = 70*5
							Else
								DropItem(SelectedItem)
								InvOpen = False
							EndIf
						Default
							DropItem(SelectedItem)
							InvOpen = False
					End Select
					
					MoveMouse viewport_center_x, viewport_center_y
				Else
					If Inventory(MouseSlot) = Null Then
						For z% = 0 To MaxItemAmount - 1
							If Inventory(z) = SelectedItem Then Inventory(z) = Null
						Next
						Inventory(MouseSlot) = SelectedItem
					ElseIf Inventory(MouseSlot) <> SelectedItem
						Select SelectedItem\itemtemplate\tempname
							Case "paper","key1","key2","key3","key4","key5","keyomni","misc","badge","ticket","quarter","coin","key","scp860"
								If Inventory(MouseSlot)\itemtemplate\tempname = "clipboard" Then
									;Add an item to clipboard
									Local added.Items = Null
									Local b$ = SelectedItem\itemtemplate\tempname
									Local b2$ = SelectedItem\itemtemplate\namespec
									If (b<>"misc" And b<>"quarter" And b<>"coin" And b<>"lkey" And b<>"scp860" And b<>"scp714") Lor (b2="keyplay" Lor b2="keymaster") Then
										For c% = 0 To Inventory(MouseSlot)\invSlots-1
											If (Inventory(MouseSlot)\SecondInv[c] = Null)
												If SelectedItem <> Null Then
													Inventory(MouseSlot)\SecondInv[c] = SelectedItem
													Inventory(MouseSlot)\state = 1.0
													SetAnimTime Inventory(MouseSlot)\model,0.0
													Inventory(MouseSlot)\invimg = Inventory(MouseSlot)\itemtemplate\invimg
													
													For ri% = 0 To MaxItemAmount - 1
														If Inventory(ri) = SelectedItem Then
															Inventory(ri) = Null
															PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
														EndIf
													Next
													added = SelectedItem
													SelectedItem = Null : Exit
												EndIf
											EndIf
										Next
										If SelectedItem <> Null Then
											Msg = GetLocalString("Messages", "clipfull")
										Else
											If added\itemtemplate\tempname = "paper" Then
												Msg = GetLocalString("Messages", "clipaddeddoc")
											ElseIf added\itemtemplate\tempname = "badge"
												Msg = GetLocalStringR("Messages", "clipaddedbadge", added\itemtemplate\localname)
											Else
												Msg = GetLocalStringR("Messages", "clipaddeditem", added\itemtemplate\localname)
											EndIf
										EndIf
									Else
										Msg = GetLocalString("Messages", "cantcombine")
									EndIf
								ElseIf Inventory(MouseSlot)\itemtemplate\tempname = "wallet" Then
									;Add an item to clipboard
									added.Items = Null
									b$ = SelectedItem\itemtemplate\tempname
									b2$ = SelectedItem\itemtemplate\namespec
									If (b<>"misc" And b<>"paper") Lor (b2="keyplay" Lor b2="keymaster") Then
										For c% = 0 To Inventory(MouseSlot)\invSlots-1
											If (Inventory(MouseSlot)\SecondInv[c] = Null)
												If SelectedItem <> Null Then
													Inventory(MouseSlot)\SecondInv[c] = SelectedItem
													Inventory(MouseSlot)\state = 1.0
													If b<>"quarter" And b<>"coin" And b<>"key" And b<>"scp860"
														SetAnimTime Inventory(MouseSlot)\model,3.0
													EndIf
													Inventory(MouseSlot)\invimg = Inventory(MouseSlot)\itemtemplate\invimg
													
													For ri% = 0 To MaxItemAmount - 1
														If Inventory(ri) = SelectedItem Then
															Inventory(ri) = Null
															PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
														EndIf
													Next
													added = SelectedItem
													SelectedItem = Null : Exit
												EndIf
											EndIf
										Next
										If SelectedItem <> Null Then
											Msg = GetLocalString("Messages", "walletfull")
										Else
											Msg = GetLocalStringR("Messages", "walletput", added\itemtemplate\localname)
										EndIf
									Else
										Msg = GetLocalString("Messages", "cantcombine")
									EndIf
								Else
									Msg = GetLocalString("Messages", "cantcombine")
								EndIf
								
							Case "badbat"
								Select Inventory(MouseSlot)\itemtemplate\tempname
									Case "badnav", "nav", "nav300", "navulti"
										If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))	
										RemoveItem (SelectedItem)
										Inventory(MouseSlot)\state = 50.0
										Msg = GetLocalString("Messages", "batnavsucc")
									Case "nav310"
										Msg = GetLocalString("Messages", "batnavult")
									Case "radio"
										If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))	
										RemoveItem (SelectedItem)
										Inventory(MouseSlot)\state = 50.0
										Msg = GetLocalString("Messages", "batradiosucc")
									Case "fineradio", "veryfineradio"
										Msg = GetLocalString("Messages", "batradioult")
									Case "18vradio"
										Msg = GetLocalString("Messages", "batradiofit")
									Case "badnvg", "nvg", "supernvg"
										If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))	
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\state = 500.0
										Msg = GetLocalString("Messages", "batnvgsucc")
									Case "finenvg"
										Msg = GetLocalString("Messages", "batnvgult")
									Case "scramble"
										If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))	
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\state = 500.0
										Msg = GetLocalString("Messages", "batscramblesucc")
									Default
										Msg = GetLocalString("Messages", "cantcombine")
								End Select
							
							Case "bat"
								Select Inventory(MouseSlot)\itemtemplate\tempname
									Case "badnav", "nav", "nav300", "navulti"
										If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))	
										RemoveItem (SelectedItem)
										Inventory(MouseSlot)\state = 100.0
										Msg = GetLocalString("Messages", "batnavsucc")
									Case "nav310"
										Msg = GetLocalString("Messages", "batnavult")
									Case "radio"
										If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))	
										RemoveItem (SelectedItem)
										Inventory(MouseSlot)\state = 100.0
										Msg = GetLocalString("Messages", "batradiosucc")
									Case "fineradio", "veryfineradio"
										Msg = GetLocalString("Messages", "batradioult")
									Case "18vradio"
										Msg = GetLocalString("Messages", "batradiofit")
									Case "badnvg", "nvg", "supernvg"
										If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))	
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\state = 1000.0
										Msg = GetLocalString("Messages", "batnvgsucc")
									Case "finenvg"
										Msg = GetLocalString("Messages", "batnvgult")
									Case "scramble"
										If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))	
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\state = 1000.0
										Msg = GetLocalString("Messages", "batscramblesucc")
									Default
										Msg = GetLocalString("Messages", "cantcombine")
								End Select
								
							Case "18vbat"
								Select Inventory(MouseSlot)\itemtemplate\tempname
									Case "badnav", "nav", "nav300", "navulti"
										Msg = GetLocalString("Messages", "batnavfit")
									Case "nav310"
										Msg = GetLocalString("Messages", "batnavult")
									Case "radio"
										Msg = GetLocalString("Messages", "batradiofit")
									Case "fineradio", "veryfineradio"
										Msg = GetLocalString("Messages", "batradioult")
									Case "18vradio"
										If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))	
										RemoveItem (SelectedItem)
										Inventory(MouseSlot)\state = 100.0
										Msg = GetLocalString("Messages", "batradiosucc")
									Default
										Msg = GetLocalString("Messages", "cantcombine")
								End Select
							
							Case "superbat", "killbat" ;If you somehow manage to pick it up ;P
								Select Inventory(MouseSlot)\itemtemplate\tempname
									Case "badnav", "nav", "nav300", "navulti"
										If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))	
										RemoveItem (SelectedItem)
										Inventory(MouseSlot)\state = 1000.0
										Msg = GetLocalString("Messages", "batnavsucc")
									Case "nav310"
										Msg = GetLocalString("Messages", "batnavult")
									Case "radio"
										If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))	
										RemoveItem (SelectedItem)
										Inventory(MouseSlot)\state = 1000.0
										Msg = GetLocalString("Messages", "batradiosucc")
									Case "fineradio", "veryfineradio"
										Msg = GetLocalString("Messages", "batradioult")
									Case "18vradio"
										Msg = GetLocalString("Messages", "batradiofit")
									Case "badnvg", "nvg", "supernvg"
										If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))	
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\state = 10000.0
										Msg = GetLocalString("Messages", "batnvgsucc")
									Case "finenvg"
										Msg = GetLocalString("Messages", "batnvgult")
									Case "scramble"
										If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))	
										RemoveItem(SelectedItem)
										Inventory(MouseSlot)\state = 10000.0
										Msg = GetLocalString("Messages", "batscramblesucc")
									Default
										Msg = GetLocalString("Messages", "cantcombine")
								End Select
							
							Default
								Msg = GetLocalString("Messages", "cantcombine")
						End Select
						MsgTimer = 70 * 5
					EndIf
					
				EndIf
				SelectedItem = Null
			EndIf
		EndIf
		
		If I_Opt\GraphicMode = 0 Then DrawImage CursorIMG, ScaledMouseX(),ScaledMouseY()
		
		If InvOpen = False Then 
			ResumeSounds() 
			MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
		EndIf
	Else ;invopen = False
		
		If SelectedItem <> Null Then
			Select SelectedItem\itemtemplate\tempname
				Case "badnvg","nvg","supernvg","finenvg"
					If SelectedItem\Picked = 2 Then
						Msg = GetLocalString("Messages", "nvgoff")
						WearingNightVision = 0
						CameraFogFar = StoredCameraFogFar
						SelectedItem\Picked = 1
					ElseIf WearingNightVision=0
						If CanUseItem(False, False) Then
							Msg = GetLocalString("Messages", "nvgon")
							WearingGasMask = 0
							Select SelectedItem\itemtemplate\tempname
								Case "badnvg"
									WearingNightVision = -1
								Case "nvg"
									WearingNightVision = 1
								Case "supernvg"
									WearingNightVision = 2
								Case "finenvg"
									WearingNightVision = 3
							End Select
							If WearingNightVision<>-1 Then
								StoredCameraFogFar = CameraFogFar
								CameraFogFar = 30
							EndIf
							SelectedItem\Picked = 2
						EndIf
					Else
						Msg = GetLocalString("Messages", "nvgdouble")
					EndIf
					SelectedItem = Null
					MsgTimer = 70 * 5
				Case "scramble"
					If SelectedItem\Picked = 2 Then
						SelectedItem\Picked = 1
						WearingScramble = False
						Msg = GetLocalString("Messages", "scrambleoff")
					ElseIf WearingScramble = False Then
						If CanUseItem(False, False)
							SelectedItem\Picked = 2
							WearingScramble = SelectedItem\state2 + 1
							Msg = GetLocalString("Messages", "scrambleon")
						EndIf
					Else
						Msg = GetLocalString("Messages", "scrambledouble")
					Endif
					SelectedItem = Null
					MsgTimer = 70 * 5
				Case "scp1123"
					If Not (Wearing714 = 1) Then
						If PlayerRoom\RoomTemplate\Name <> "room1123" Then
							ShowEntity Light
							LightFlash = 7
							PlaySound_Strict(LoadTempSound("SFX\SCP\1123\Touch.ogg"))		
							DeathMSG = GetLocalString("Deaths", "1123")
							Kill()
							Return
						EndIf
						For e.Events = Each Events
							If e\EventName = "room1123" Then 
								If e\EventState = 0 Then
									ShowEntity Light
									LightFlash = 3
									PlaySound_Strict(LoadTempSound("SFX\SCP\1123\Touch.ogg"))
								EndIf
								e\EventState = Max(1, e\EventState)
								RemoveItem(SelectedItem)
								Exit
							EndIf
						Next
					EndIf
				Case "key1", "key2", "key3", "key4", "key5", "keyomni", "keyomni", "scp860", "hand", "hand2", "hand3", "quarter"
					DrawImage(SelectedItem\itemtemplate\invimg, I_Opt\GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, I_Opt\GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
				Case "scp513"
				
					Local npc513.NPCs

					PlaySound_Strict LoadTempSound("SFX\SCP\513\Bell1.ogg")
					
					If SelectedItem\state = 0 Then
						npc513 = CreateNPC(NPCtype5131, 0,0,0)
						SelectedItem\state = npc513\ID
					EndIf	
					SelectedItem = Null

				Case "scp500"

					If CanUseItem(False, True)
						GiveAchievement(Achv500)
						
						If Infect > 0 Then
							Msg = GetLocalString("Messages", "500nausea")
						Else
							Msg = GetLocalString("Messages", "pillswallow")
						EndIf
						MsgTimer = 70*7
						
						DeathTimer = 0
						Infect = 0
						Stamina = 100
						For i = 0 To 6
							SCP1025state[i]=0
						Next
						If StaminaEffect > 1.0 Then
							StaminaEffect = 1.0
							StaminaEffectTimer = 0.0
						EndIf
						
						RemoveItem(SelectedItem)
						SelectedItem = Null
					EndIf	

				Case "veryfinefirstaid"

					If CanUseItem(False, True)
						Select Rand(5)
							Case 1
								Injuries = 3.5
								Msg = Msg = GetLocalString("Messages", "faidbleed")
								MsgTimer = 70*7
							Case 2
								Injuries = 0
								Bloodloss = 0
								Msg = Msg = GetLocalString("Messages", "faidheal")
								MsgTimer = 70*7
							Case 3
								Injuries = Max(0, Injuries - Rnd(0.5,3.5))
								Bloodloss = Max(0, Bloodloss - Rnd(10,100))
								Msg = Msg = GetLocalString("Messages", "faidbetter")
								MsgTimer = 70*7
							Case 4
								BlurTimer = 10000
								Bloodloss = 0
								Msg = GetLocalString("Messages", "faidnausea")
								MsgTimer = 70*7
							Case 5
								BlinkTimer = -10
								Local roomname$ = PlayerRoom\RoomTemplate\Name
								If roomname = "dimension1499" Lor roomname = "gatea" Lor (roomname="gateb" And EntityY(Collider)>1040.0*RoomScale-2000)
									Injuries = 2.5
									Msg = GetLocalString("Messages", "faidbleed")
									MsgTimer = 70*7
								Else
									For r.Rooms = Each Rooms
										If r\RoomTemplate\Name = "pocketdimension" Then
											PositionEntity(Collider, EntityX(r\obj),0.8,EntityZ(r\obj))		
											ResetEntity Collider									
											UpdateDoors()
											UpdateRooms()
											PlaySound_Strict(Use914SFX)
											DropSpeed = 0
											Curr106\State = -2500
											Exit
										EndIf
									Next
									Msg = GetLocalString("Messages", "faidpocketd")
									MsgTimer = 70*8
								EndIf
						End Select
						
						RemoveItem(SelectedItem)
					EndIf

				Case "firstaid", "finefirstaid", "firstaid2"

					If Bloodloss = 0 And Injuries = 0 Then
						Msg = GetLocalString("Messages", "faidno")
						MsgTimer = 70*5
						SelectedItem = Null
					Else
						If CanUseItem(True, True)
							CurrSpeed = CurveValue(0, CurrSpeed, 5.0)
							SetCrouch(True)
							
							DrawImage(SelectedItem\itemtemplate\invimg, I_Opt\GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, I_Opt\GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
							
							;width = 300
							;height = 20
							x% = I_Opt\GraphicWidth / 2 - 300 / 2
							y% = I_Opt\GraphicHeight / 2 + 80
							Rect(x, y, 300+4, 20, False)
							For  i% = 1 To Int((300 - 2) * (SelectedItem\state / 100.0) / 10)
								DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
							Next
							
							SelectedItem\state = Min(SelectedItem\state+(FPSfactor/5.0),100)			
							
							If SelectedItem\state = 100 Then
								If SelectedItem\itemtemplate\tempname = "finefirstaid" Then
									Bloodloss = 0
									Injuries = Max(0, Injuries - 2.0)
									If Injuries = 0 Then
										Msg = GetLocalString("Messagse", "faidbandpainfine")
									ElseIf Injuries > 1.0
										Msg = GetLocalString("Messagse", "faidbandpainbleed")
									Else
										Msg = GetLocalString("Messagse", "faidbandpainsore")
									EndIf
									MsgTimer = 70*5
									RemoveItem(SelectedItem)
								Else
									Bloodloss = Max(0, Bloodloss - Rand(10,20))
									If Injuries => 2.5 Then
										Msg = GetLocalString("Messagse", "faidtoosevere")
										Injuries = Max(2.5, Injuries-Rnd(0.3,0.7))
									ElseIf Injuries > 1.0
										Injuries = Max(0.5, Injuries-Rnd(0.5,1.0))
										If Injuries > 1.0 Then
											Msg = GetLocalString("Messagse", "faidbandbleed")
										Else
											Msg = GetLocalString("Messagse", "faidbleedstop")
										EndIf
									Else
										If Injuries > 0.5 Then
											Injuries = 0.5
											Msg = GetLocalString("Messagse", "faidpainease")
										Else
											Injuries = 0.5
											Msg = GetLocalString("Messagse", "faidpainhurt")
										EndIf
									EndIf
									
									If SelectedItem\itemtemplate\tempname = "firstaid2" Then 
										Select Rand(6)
											Case 1
												I_Cheats\SuperMan = True
												Msg = GetLocalString("Messagse", "faidwoo")
											Case 2
												InvertCam = (Not InvertCam)
												Msg = GetLocalString("Messagse", "faidturn")
											Case 3
												BlurTimer = 5000
												Msg = GetLocalString("Messagse", "faidnausea")
											Case 4
												BlinkEffect = 0.6
												BlinkEffectTimer = Rand(20,30)
											Case 5
												Bloodloss = 0
												Injuries = 0
												Msg = GetLocalString("Messagse", "faidbandfine")
											Case 6
												Msg = GetLocalString("Messagse", "faidbandbleedheavy")
												Injuries = 3.5
										End Select
									EndIf
									
									MsgTimer = 70*5
									RemoveItem(SelectedItem)
								EndIf							
							EndIf
						EndIf
					EndIf

				Case "eyedrops"

					If CanUseItem(False,False)
						If (Not (Wearing714=1)) Then ;wtf is this
							BlinkEffect = 0.6
							BlinkEffectTimer = Rand(20,30)
							BlurTimer = 200
						EndIf
						RemoveItem(SelectedItem)
					EndIf

				Case "fineeyedrops"

					If CanUseItem(False,False)
						If (Not (Wearing714=1)) Then 
							BlinkEffect = 0.4
							BlinkEffectTimer = Rand(30,40)
							Bloodloss = Max(Bloodloss-1.0, 0)
							BlurTimer = 200
						EndIf
						RemoveItem(SelectedItem)
					EndIf

				Case "supereyedrops"

					If CanUseItem(False,False)
						If (Not (Wearing714 = 1)) Then
							BlinkEffect = 0.0
							BlinkEffectTimer = 60
							EyeStuck = 10000
						EndIf
						BlurTimer = 1000
						RemoveItem(SelectedItem)
					EndIf

				Case "paper", "ticket"

					If SelectedItem\itemtemplate\img = 0 Then
						Select SelectedItem\itemtemplate\namespec
							Case "burnt"
								SelectedItem\itemtemplate\img = LoadImage_Strict("GFX\items\bn.it")
								SetBuffer ImageBuffer(SelectedItem\itemtemplate\img)
								Color 0,0,0
								Text 277, 469, AccessCode, True, True
								Color 255,255,255
								SetBuffer BackBuffer()
							Case "d372"
								SelectedItem\itemtemplate\img = LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
								SelectedItem\itemtemplate\img = ResizeImage2(SelectedItem\itemtemplate\img, ImageWidth(SelectedItem\itemtemplate\img) * MenuScale, ImageHeight(SelectedItem\itemtemplate\img) * MenuScale)
								
								SetBuffer ImageBuffer(SelectedItem\itemtemplate\img)
								Color 37,45,137
								SetFont I_Opt\Fonts[5]
								temp = ((Int(AccessCode)*3) Mod 10000)
								If temp < 1000 Then temp = temp+1000
								Text 383*MenuScale, 734*MenuScale, temp, True, True
								Color 255,255,255
								SetBuffer BackBuffer()
							Case "ticket"
								;don't resize because it messes up the masking
								SelectedItem\itemtemplate\img = LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
								
								If (SelectedItem\state = 0) Then
									Msg = GetLocalString("Messages", "nostalgiamovie")
									MsgTimer = 70*10
									PlaySound_Strict LoadTempSound("SFX\SCP\1162\NostalgiaCancer"+Rand(1,5)+".ogg")
									SelectedItem\state = 1
								EndIf
							Case "d990"
								; Resizing does mess with masking!
								SelectedItem\itemtemplate\img = LoadImage_Strict(SelectedItem\itemtemplate\imgpath)
							Case "970ds"
								SelectedItem\itemtemplate\img=LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
								SelectedItem\itemtemplate\img = ResizeImage2(SelectedItem\itemtemplate\img, ImageWidth(SelectedItem\itemtemplate\img) * MenuScale, ImageHeight(SelectedItem\itemtemplate\img) * MenuScale)
								If (SelectedItem\state = 1) Then
									Msg = GetLocalString("Messages", "nostalgiadoc")
									MsgTimer = 70*10
									PlaySound_Strict LoadTempSound("SFX\SCP\1162\NostalgiaCancer"+Rand(1,5)+".ogg")
									SelectedItem\state = 0
								EndIf
							Case "hearing"
								If SelectedItem\itemtemplate\img = 0 Then
									SelectedItem\itemtemplate\img = LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
									SelectedItem\itemtemplate\img = ResizeImage2(SelectedItem\itemtemplate\img, ImageWidth(SelectedItem\itemtemplate\img) * MenuScale, ImageHeight(SelectedItem\itemtemplate\img) * MenuScale)
									
									MaskImage(SelectedItem\itemtemplate\img, 255, 0, 255)
								EndIf
								
								If SelectedItem\state = 0
									BlurTimer = 1000
									
									Msg = GetLocalString("Messages", "nostalgiapaper")
									MsgTimer = 70*10
									PlaySound_Strict LoadTempSound("SFX\SCP\1162\NostalgiaCancer"+Rand(6,10)+".ogg")
									SelectedItem\state = 1
								EndIf
							Default
								SelectedItem\itemtemplate\img = LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
								SelectedItem\itemtemplate\img = ResizeImage2(SelectedItem\itemtemplate\img, ImageWidth(SelectedItem\itemtemplate\img) * MenuScale, ImageHeight(SelectedItem\itemtemplate\img) * MenuScale)
						End Select
						
						MaskImage(SelectedItem\itemtemplate\img, 255, 0, 255)
					EndIf
					
					DrawImage(SelectedItem\itemtemplate\img, I_Opt\GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\img) / 2, I_Opt\GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\img) / 2)

				Case "scp1025"
					GiveAchievement(Achv1025)
					If SelectedItem\itemtemplate\img=0 Then
						SelectedItem\itemtemplate\img=LoadImage_Strict("GFX\items\1025\1025_"+Int(SelectedItem\state)+".jpg")	
						SelectedItem\itemtemplate\img = ResizeImage2(SelectedItem\itemtemplate\img, ImageWidth(SelectedItem\itemtemplate\img) * MenuScale, ImageHeight(SelectedItem\itemtemplate\img) * MenuScale)
						
						If (Not Wearing714) Then
							If (SelectedItem\state = 7) Then
								If Infect = 0 Then Infect = 1
							Else
								SCP1025state[SelectedItem\state]=Max(1,SCP1025state[SelectedItem\state])
								SCP1025state[7] = 1 + (SelectedItem\state2 = 2)*2 ;3x as fast if VERY_FINE
							EndIf
						EndIf
						If Rand(3-(SelectedItem\state2<>2)*SelectedItem\state2) = 1 Then ;higher chance for good illness if FINE, lower change for good illness if COARSE
							SelectedItem\state = 6
						Else
							SelectedItem\state = Rand(0,7)
						EndIf
						MaskImage(SelectedItem\itemtemplate\img, 255, 0, 255)
					EndIf
					
					DrawImage(SelectedItem\itemtemplate\img, I_Opt\GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\img) / 2, I_Opt\GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\img) / 2)

				Case "book"
					Msg = GetLocalString("Messages", "readbook")
					MsgTimer = 70*5
					
				Case "cup"

					If CanUseItem(False,True)
						SelectedItem\localname = Trim(Lower(SelectedItem\localname))
						If Left(SelectedItem\localname, Min(Len(GetLocalString("Items", "fullcup")),Len(SelectedItem\localname))) = Lower(GetLocalString("Items", "fullcup")) Then
							SelectedItem\localname = Right(SelectedItem\localname, Len(SelectedItem\localname)-(Len(GetLocalString("Items", "fullcup"))+1))
						EndIf
						
						Local loc% = GetINISectionLocation(Data294, SelectedItem\localname)
						
						strtemp = GetINIString2(Data294, loc, "message")
						If strtemp <> "" Then Msg = strtemp : MsgTimer = 70*6
						
						If GetINIInt2(Data294, loc, "lethal") Lor GetINIInt2(Data294, loc, "deathtimer") Then 
							DeathMSG = GetINIString2(Data294, loc, "deathmessage")
							If GetINIInt2(Data294, loc, "lethal") Then Kill()
						EndIf
						BlurTimer = GetINIInt2(Data294, loc, "blur")*70*SelectedItem\state
						If VomitTimer = 0 Then VomitTimer = GetINIInt2(Data294, loc, "vomit")
						CameraShakeTimer = GetINIString2(Data294, loc, "camerashake")
						Injuries = Max(Injuries + GetINIInt2(Data294, loc, "damage"),0)*SelectedItem\state
						Bloodloss = Max(Bloodloss + GetINIInt2(Data294, loc, "blood loss"),0)*SelectedItem\state
						strtemp =  GetINIString2(Data294, loc, "sound")
						If strtemp<>"" Then
							PlaySound_Strict LoadTempSound(strtemp)
						EndIf
						If GetINIInt2(Data294, loc, "stomachache") Then SCP1025state[3]=1
						
						DeathTimer=GetINIInt2(Data294, loc, "deathtimer")*70
						
						BlinkEffect = Float(GetINIString2(Data294, loc, "blink effect", 1.0))*SelectedItem\state
						BlinkEffectTimer = Float(GetINIString2(Data294, loc, "blink effect timer", 1.0))*SelectedItem\state
						
						StaminaEffect = Float(GetINIString2(Data294, loc, "stamina effect", 1.0))*SelectedItem\state
						StaminaEffectTimer = Float(GetINIString2(Data294, loc, "stamina effect timer", 1.0))*SelectedItem\state
						
						strtemp = GetINIString2(Data294, loc, "refusemessage")
						If strtemp <> "" Then
							Msg = strtemp 
							MsgTimer = 70*6		
						Else
							it.Items = CreateItem("emptycup", 0,0,0)
							it\Picked = 1
							For i = 0 To MaxItemAmount-1
								If Inventory(i)=SelectedItem Then Inventory(i) = it : Exit
							Next					
							EntityType (it\collider, HIT_ITEM)
							
							RemoveItem(SelectedItem)						
						EndIf
						
						SelectedItem = Null
					EndIf

				Case "syringe"

					If CanUseItem(True,True)
						HealTimer = 30
						StaminaEffect = 0.5
						StaminaEffectTimer = 20
						
						Msg = GetLocalString("Messages", "syringeadrenalineslight")
						MsgTimer = 70 * 8
						
						RemoveItem(SelectedItem)
					EndIf

				Case "finesyringe"

					If CanUseItem(True,True)
						HealTimer = Rnd(20, 40)
						StaminaEffect = Rnd(0.5, 0.8)
						StaminaEffectTimer = Rnd(20, 30)
						
						Msg = GetLocalString("Messages", "syringeadrenaline")
						MsgTimer = 70 * 8
						
						RemoveItem(SelectedItem)
					EndIf

				Case "veryfinesyringe"

					If CanUseItem(True,True)
						Select Rand(3)
							Case 1
								HealTimer = Rnd(40, 60)
								StaminaEffect = 0.1
								StaminaEffectTimer = 30
								Msg = GetLocalString("Messages", "syringeadrenalinehuge")
							Case 2
								I_Cheats\SuperMan = True
								Msg = GetLocalString("Messages", "syringeadrenalinehumo")
							Case 3
								VomitTimer = 30
								Msg = GetLocalString("Messages", "syringepain")
						End Select
						
						MsgTimer = 70 * 8
						RemoveItem(SelectedItem)
					EndIf

				Case "radio","18vradio","fineradio","veryfineradio"

					If SelectedItem\itemtemplate\tempname <> "fineradio" And SelectedItem\itemtemplate\tempname <> "veryfineradio" Then SelectedItem\state = Max(0, SelectedItem\state - FPSfactor * 0.004)
					
					If SelectedItem\itemtemplate\img=0 Then
						SelectedItem\itemtemplate\img=LoadImage_Strict(SelectedItem\itemtemplate\imgpath)	
						MaskImage(SelectedItem\itemtemplate\img, 255, 0, 255)
					EndIf
					
					;RadioState[5] = has the "use the number keys" -message been shown yet (true/false)
					;RadioState[6] = a timer for the "code channel"
					;RadioState[7] = another timer for the "code channel"
					
					If RadioState[5] = 0 Then 
						Msg = GetLocalString("Messages", "radiohelp")
						MsgTimer = 70 * 5
						RadioState[5] = 1
						RadioState[0] = -1
					EndIf
					
					strtemp$ = ""
					
					x = I_Opt\GraphicWidth - ImageWidth(SelectedItem\itemtemplate\img) ;+ 120
					y = I_Opt\GraphicHeight - ImageHeight(SelectedItem\itemtemplate\img) ;- 30
					
					DrawImage(SelectedItem\itemtemplate\img, x, y)
					
					If SelectedItem\state > 0 Lor SelectedItem\itemtemplate\tempname = "fineradio" Lor SelectedItem\itemtemplate\tempname = "veryfineradio" Then
						If CoffinDistance < 4.0 Lor PlayerRoom\RoomTemplate\Name = "pocketdimension" Then
							ResumeChannel(RadioCHN[5])
							If ChannelPlaying(RadioCHN[5]) = False Then RadioCHN[5] = PlaySound_Strict(RadioStatic)	
						Else
							Select Int(SelectedItem\state2)
								Case 0 ;randomkanava
									ResumeChannel(RadioCHN[0])
									strtemp = "		"+GetLocalString("Game", "radio1utp")+" - "
									If (Not EnableUserTracks)
										If ChannelPlaying(RadioCHN[0]) = False Then RadioCHN[0] = PlaySound_Strict(RadioStatic)
										strtemp = strtemp + GetLocalString("Game", "radio1ne")+"	 "
									ElseIf UserTrackMusicAmount<1
										If ChannelPlaying(RadioCHN[0]) = False Then RadioCHN[0] = PlaySound_Strict(RadioStatic)
										strtemp = strtemp + GetLocalString("Game", "radio1ntf")+"	 "
									Else
										If (Not ChannelPlaying(RadioCHN[0]))
											If (Not UserTrackFlag%)
												If UserTrackMode
													If RadioState[0]<(UserTrackMusicAmount-1)
														RadioState[0] = RadioState[0] + 1
													Else
														RadioState[0] = 0
													EndIf
													UserTrackFlag = True
												Else
													RadioState[0] = Rand(0,UserTrackMusicAmount-1)
												EndIf
											EndIf
											If CurrUserTrack%<>0 Then FreeSound_Strict(CurrUserTrack%) : CurrUserTrack% = 0
											CurrUserTrack% = LoadSound_Strict("SFX\Radio\UserTracks\"+UserTrackName$(RadioState[0]))
											RadioCHN[0] = PlaySound_Strict(CurrUserTrack%)
											DebugLog "CurrTrack: "+RadioState[0]
											DebugLog UserTrackName$(RadioState[0])
										Else
											strtemp = strtemp + Upper(UserTrackName$(RadioState[0])) + "		  "
											UserTrackFlag = False
										EndIf
										
										If KeyHit(2) Then
											PlaySound_Strict RadioSquelch
											If (Not UserTrackFlag%)
												If UserTrackMode
													If RadioState[0]<(UserTrackMusicAmount-1)
														RadioState[0] = RadioState[0] + 1
													Else
														RadioState[0] = 0
													EndIf
													UserTrackFlag = True
												Else
													RadioState[0] = Rand(0,UserTrackMusicAmount-1)
												EndIf
											EndIf
											If CurrUserTrack%<>0 Then FreeSound_Strict(CurrUserTrack%) : CurrUserTrack% = 0
											CurrUserTrack% = LoadSound_Strict("SFX\Radio\UserTracks\"+UserTrackName$(RadioState[0]))
											RadioCHN[0] = PlaySound_Strict(CurrUserTrack%)
											DebugLog "CurrTrack: "+RadioState[0]
											DebugLog UserTrackName$(RadioState[0])
										EndIf
									EndIf
								Case 1 ;hälytyskanava
									DebugLog RadioState[1] 
									
									ResumeChannel(RadioCHN[1])
									strtemp = "		"+GetLocalString("Game", "radio2")+"		  "
									If ChannelPlaying(RadioCHN[1]) = False Then
										
										If RadioState[1] => 5 Then
											RadioCHN[1] = PlaySound_Strict(RadioSFX(1,1))	
											RadioState[1] = 0
										Else
											RadioState[1]=RadioState[1]+1	
											RadioCHN[1] = PlaySound_Strict(RadioSFX(1,0))	
										EndIf
										
									EndIf
									
								Case 2 ;scp-radio
									ResumeChannel(RadioCHN[2])
									strtemp = "		"+GetLocalString("Game", "radio3")+"		  "
									If ChannelPlaying(RadioCHN[2]) = False Then
										RadioState[2]=RadioState[2]+1
										If RadioState[2] = 17 Then RadioState[2] = 1
										If Floor(RadioState[2]/2)=Ceil(RadioState[2]/2) Then ;parillinen, soitetaan normiviesti
											RadioCHN[2] = PlaySound_Strict(RadioSFX(2,Int(RadioState[2]/2)))	
										Else ;pariton, soitetaan musiikkia
											RadioCHN[2] = PlaySound_Strict(RadioSFX(2,0))
										EndIf
									EndIf 
								Case 3
									ResumeChannel(RadioCHN[3])
									strtemp = "			 "+GetLocalString("Game", "radio4")+"		 "
									If ChannelPlaying(RadioCHN[3]) = False Then RadioCHN[3] = PlaySound_Strict(RadioStatic)
									
									If MTFtimer > 0 Then 
										RadioState[3]=RadioState[3]+Max(Rand(-10,1),0)
										Select RadioState[3]
											Case 40
												If Not RadioState3[0] Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random1.ogg"))
													RadioState[3] = RadioState[3]+1	
													RadioState3[0] = True	
												EndIf											
											Case 400
												If Not RadioState3[1] Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random2.ogg"))
													RadioState[3] = RadioState[3]+1	
													RadioState3[1] = True	
												EndIf	
											Case 800
												If Not RadioState3[2] Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random3.ogg"))
													RadioState[3] = RadioState[3]+1	
													RadioState3[2] = True
												EndIf													
											Case 1200
												If Not RadioState3[3] Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random4.ogg"))	
													RadioState[3] = RadioState[3]+1	
													RadioState3[3] = True
												EndIf
											Case 1600
												If Not RadioState3[4] Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random5.ogg"))	
													RadioState[3] = RadioState[3]+1
													RadioState3[4] = True
												EndIf
											Case 2000
												If Not RadioState3[5] Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random6.ogg"))	
													RadioState[3] = RadioState[3]+1
													RadioState3[5] = True
												EndIf
											Case 2400
												If Not RadioState3[6] Then
													RadioCHN[3] = PlaySound_Strict(LoadTempSound("SFX\Character\MTF\Random7.ogg"))	
													RadioState[3] = RadioState[3]+1
													RadioState3[6] = True
												EndIf
										End Select
									EndIf
								Case 4
									ResumeChannel(RadioCHN[6]) ;taustalle kohinaa
									If ChannelPlaying(RadioCHN[6]) = False Then RadioCHN[6] = PlaySound_Strict(RadioStatic)									
									
									ResumeChannel(RadioCHN[4])
									If ChannelPlaying(RadioCHN[4]) = False Then 
										If RemoteDoorOn = False And RadioState[8] = False Then
											RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\radio\Chatter3.ogg"))	
											RadioState[8] = True
										Else
											RadioState[4]=RadioState[4]+Max(Rand(-10,1),0)
											
											Select RadioState[4]
												Case 10
													If (Not Contained106)
														If Not RadioState4[0] Then
															RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\radio\OhGod.ogg"))
															RadioState[4] = RadioState[4]+1
															RadioState4[0] = True
														EndIf
													EndIf
												Case 100
													If Not RadioState4[1] Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\radio\Chatter2.ogg"))
														RadioState[4] = RadioState[4]+1
														RadioState4[1] = True
													EndIf		
												Case 158
													If MTFtimer = 0 And (Not RadioState4[2]) Then 
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\radio\franklin1.ogg"))
														RadioState[4] = RadioState[4]+1
														RadioState[2] = True
													EndIf
												Case 200
													If Not RadioState4[3] Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\radio\Chatter4.ogg"))
														RadioState[4] = RadioState[4]+1
														RadioState4[3] = True
													EndIf		
												Case 260
													If Not RadioState4[4] Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\SCP\035\RadioHelp1.ogg"))
														RadioState[4] = RadioState[4]+1
														RadioState4[4] = True
													EndIf		
												Case 300
													If Not RadioState4[5] Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\radio\Chatter1.ogg"))	
														RadioState[4] = RadioState[4]+1	
														RadioState4[5] = True
													EndIf		
												Case 350
													If Not RadioState4[6] Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\radio\franklin2.ogg"))
														RadioState[4] = RadioState[4]+1
														RadioState4[6] = True
													EndIf		
												Case 400
													If Not RadioState4[7] Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\SCP\035\RadioHelp2.ogg"))
														RadioState[4] = RadioState[4]+1
														RadioState4[7] = True
													EndIf		
												Case 450
													If Not RadioState4[8] Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\radio\franklin3.ogg"))	
														RadioState[4] = RadioState[4]+1		
														RadioState4[8] = True
													EndIf		
												Case 600
													If Not RadioState4[9] Then
														RadioCHN[4] = PlaySound_Strict(LoadTempSound("SFX\radio\franklin4.ogg"))	
														RadioState[4] = RadioState[4]+1	
														RadioState4[9] = True
													EndIf		
											End Select
										EndIf
									EndIf
									
									
								Case 5
									ResumeChannel(RadioCHN[5])
									If ChannelPlaying(RadioCHN[5]) = False Then RadioCHN[5] = PlaySound_Strict(RadioStatic)
							End Select 
							
							x=x+66
							y=y+419
							
							Color (30,30,30)
							
							If SelectedItem\itemtemplate\tempname <> "fineradio" And SelectedItem\itemtemplate\tempname <> "veryfineradio" Then
								;Text (x - 60, y - 20, "BATTERY")
								For i = 0 To 4
									Rect(x, y+8*i, 43 - i * 6, 4, Ceil(SelectedItem\state / 20.0) > 4 - i )
								Next
							EndIf	
							
							SetFont I_Opt\Fonts[3]
							Text(x+60, y, GetLocalString("Game", "radiochn"))						
							
							If SelectedItem\itemtemplate\tempname = "veryfineradio" Then ;"KOODIKANAVA"
								ResumeChannel(RadioCHN[0])
								If ChannelPlaying(RadioCHN[0]) = False Then RadioCHN[0] = PlaySound_Strict(RadioStatic)
								
								;RadioState[7]=kuinka mones piippaus menossa
								;RadioState[8]=kuinka mones access coden numero menossa
								RadioState[6]=RadioState[6] + FPSfactor
								temp = Mid(Str(AccessCode),RadioState[8]+1,1)
								If RadioState[6]-FPSfactor =< RadioState[7]*50 And RadioState[6]>RadioState[7]*50 Then
									PlaySound_Strict(RadioBuzz)
									RadioState[7]=RadioState[7]+1
									If RadioState[7]=>temp Then
										RadioState[7]=0
										RadioState[6]=-100
										RadioState[8]=RadioState[8]+1
										If RadioState[8]=4 Then RadioState[8]=0 : RadioState[6]=-200
									EndIf
								EndIf
								
								strtemp = ""
								For i = 0 To Rand(5, 30)
									strtemp = strtemp + Chr(Rand(1,100))
								Next
								
								SetFont I_Opt\Fonts[4]
								Text(x+97, y+16, Rand(0,9),True,True)
								
							Else
								For i = 2 To 6
									If KeyHit(i) Then
										If SelectedItem\state2 <> i-2 Then ;pausetetaan nykyinen radiokanava
											PlaySound_Strict RadioSquelch
											If RadioCHN[Int(SelectedItem\state2)] <> 0 Then PauseChannel(RadioCHN[Int(SelectedItem\state2)])
										EndIf
										SelectedItem\state2 = i-2
										;jos nykyistä kanavaa ollaan soitettu, laitetaan jatketaan toistoa samasta kohdasta
										If RadioCHN[SelectedItem\state2]<>0 Then ResumeChannel(RadioCHN[SelectedItem\state2])
									EndIf
								Next
								
								SetFont I_Opt\Fonts[4]
								Text(x+97, y+16, Int(SelectedItem\state2+1),True,True)
							EndIf
							
							SetFont I_Opt\Fonts[3]
							If strtemp <> "" Then
								strtemp = Right(Left(strtemp, (Int(MilliSecs()/300) Mod Len(strtemp))),10)
								Text(x+32, y+33, strtemp)
							EndIf
							
							SetFont I_Opt\Fonts[1]
							
						EndIf
						
					EndIf

				Case "cigarette"

					If CanUseItem(False,True)
						If SelectedItem\state = 0 Then
							Local tempCig = Rand(6)
							Msg = GetLocalString("Messages", "cig" + tempCig)
							If tempCig > 4 Then
								RemoveItem(SelectedItem)
							EndIf
							SelectedItem\state = 1 
						Else
							Msg = GetLocalString("Messages", "cig2")
						EndIf
						
						MsgTimer = 70 * 5
					EndIf

				Case "scp420j"

					If CanUseItem(False,True)
						If Wearing714=1 Then
							Msg = GetLocalString("Messages", "420j714")
						Else
							Msg = GetLocalString("Messages", "420j")
							Injuries = Max(Injuries-0.5, 0)
							BlurTimer = 500
							GiveAchievement(Achv420)
							PlaySound_Strict LoadTempSound("SFX\Music\420J.ogg")
						EndIf
						MsgTimer = 70 * 5
						RemoveItem(SelectedItem)
					EndIf

				Case "420s"

					If CanUseItem(False,True)
						If Wearing714=1 Then
							Msg = GetLocalString("Messages", "420j714")
						Else
							DeathMSG = GetLocalString("Deaths", "420s")
							Msg = GetLocalString("Messages", "420js")
							KillTimer = -1						
						EndIf
						MsgTimer = 70 * 6
						RemoveItem(SelectedItem)
					EndIf
					
				Case "scp178"		
					If SelectedItem\Picked = 2 Then
						Msg = GetLocalString("Messages", "178remove")
						Msg = "You removed the glasses."
						Wearing178 = 0
						SelectedItem\Picked = 1
					ElseIf Wearing178 = 0
						GiveAchievement(Achv178)
						Msg = GetLocalString("Messages", "178puton")
						Wearing178 = SelectedItem\State + 1
						SelectedItem\Picked = 2
					Else
						Msg = GetLocalString("Messages", "178double")
					EndIf
					MsgTimer = 70 * 5
					SelectedItem = Null

				Case "scp714"

					If SelectedItem\Picked = 2 Then
						Msg = GetLocalString("Messages", "714remove")
						Wearing714 = False
						SelectedItem\Picked = 1
					ElseIf Wearing714 = False
						GiveAchievement(Achv714)
						Msg = GetLocalString("Messages", "714puton")
						Wearing714 = True
						SelectedItem\Picked = 2
					Else
						Msg = GetLocalString("Messages", "714double")
					EndIf
					MsgTimer = 70 * 5
					SelectedItem = Null	

				Case "hazmat", "hazmat2", "hazmat3"

					If WearingVest = 0 Then
						CurrSpeed = CurveValue(0, CurrSpeed, 5.0)
						
						DrawImage(SelectedItem\itemtemplate\invimg, I_Opt\GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, I_Opt\GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
						
						;width = 300
						;height = 20
						x% = I_Opt\GraphicWidth / 2 - 300 / 2
						y% = I_Opt\GraphicHeight / 2 + 80
						Rect(x, y, 300+4, 20, False)
						For  i% = 1 To Int((300 - 2) * (SelectedItem\state / 100.0) / 10)
							DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
						Next
						
						SelectedItem\state = Min(SelectedItem\state+(FPSfactor/4.0),100)
						
						If SelectedItem\state=100 Then
							If WearingHazmat<>0 Then
								Msg = GetLocalString("Messages", "hazmatremove")
								WearingHazmat = False
								DropItem(SelectedItem)
							Else
								If SelectedItem\itemtemplate\tempname="hazmat0" Then
									WearingHazmat = -1
								ElseIf SelectedItem\itemtemplate\tempname="hazmat" Then
									WearingHazmat = 1
								ElseIf SelectedItem\itemtemplate\tempname="hazmat2" Then
									WearingHazmat = 2
								Else
									WearingHazmat = 3
								EndIf
								If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
								Msg = GetLocalString("Messages", "hazmatputon")
								If WearingNightVision Then CameraFogFar = StoredCameraFogFar
								WearingGasMask = 0
								WearingNightVision = 0
								SelectedItem\Picked = 2
							EndIf
							SelectedItem\state=0
							MsgTimer = 70 * 5
							SelectedItem = Null
						EndIf
					EndIf

				Case "badvest","vest","finevest"

					CurrSpeed = CurveValue(0, CurrSpeed, 5.0)
					
					DrawImage(SelectedItem\itemtemplate\invimg, I_Opt\GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, I_Opt\GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
					
					;width = 300
					;height = 20
					x% = I_Opt\GraphicWidth / 2 - 300 / 2
					y% = I_Opt\GraphicHeight / 2 + 80
					Rect(x, y, 300+4, 20, False)
					For  i% = 1 To Int((300 - 2) * (SelectedItem\state / 100.0) / 10)
						DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
					Next
					
					SelectedItem\state = Min(SelectedItem\state+(FPSfactor/(2.0+(0.5*(SelectedItem\itemtemplate\tempname="finevest")))),100)
					
					If SelectedItem\state=100 Then
						If WearingVest<>0 Then
							Msg = GetLocalString("Messages", "vestremove")
							WearingVest = 0
							DropItem(SelectedItem)
							SelectedItem\Picked = 0
						Else
							If SelectedItem\itemtemplate\tempname="vest" Then
								Msg = GetLocalString("Messages", "vestslightly")
								WearingVest = 1
							ElseIf SelectedItem\itemtemplate\tempname="finevest"
								Msg = GetLocalString("Messages", "vesthighly")
								WearingVest = 2
							Else
								Msg = GetLocalString("Messages", "vestweird")
								WearingVest = -1
							EndIf
							If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
							SelectedItem\Picked = 2
						EndIf
						SelectedItem\state=0
						MsgTimer = 70 * 5
						SelectedItem = Null

					EndIf

				Case "badgasmask", "gasmask", "supergasmask", "heavygasmask"

					If SelectedItem\Picked = 2 Then
						Msg = GetLocalString("Messages", "gasmaskremove")
						WearingGasMask = 0
						SelectedItem\Picked = 1
					ElseIf WearingGasMask=0
						If CanUseItem(False, False) Then
							Select SelectedItem\itemtemplate\tempname
								Case "badgasmask"
									Msg = GetLocalString("Messages", "gasmaskharder")
									WearingGasMask = -1
								Case "gasmask"
									Msg = GetLocalString("Messages", "gasmaskputon")
									WearingGasMask = 1
								Case "supergasmask"
									Msg = GetLocalString("Messages", "gasmaskeasier")
									WearingGasMask = 2
								Case "heavygasmask"
									Msg = GetLocalString("Messages", "gasmaskputon")
								WearingGasMask = 3
							End Select
							SelectedItem\Picked = 2
						Endif
					Else
						Msg = GetLocalString("Messages", "gasmaskdouble")
					EndIf
					SelectedItem = Null
					MsgTimer = 70 * 5

				Case "badnav", "nav", "nav300", "nav310", "navulti"

					
					If SelectedItem\itemtemplate\img=0 Then
						SelectedItem\itemtemplate\img=LoadImage_Strict(SelectedItem\itemtemplate\imgpath)
						MaskImage(SelectedItem\itemtemplate\img, 255, 0, 255)
					EndIf
					
					If SelectedItem\itemtemplate\tempname <> "nav310" Then SelectedItem\state = Max(0, SelectedItem\state - FPSfactor * 0.005)
					
					x = I_Opt\GraphicWidth - ImageWidth(SelectedItem\itemtemplate\img)*0.5+20
					y = I_Opt\GraphicHeight - ImageHeight(SelectedItem\itemtemplate\img)*0.4-85
					
					Local PlayerX,PlayerZ
					
					DrawImage(SelectedItem\itemtemplate\img, x - ImageWidth(SelectedItem\itemtemplate\img) / 2, y - ImageHeight(SelectedItem\itemtemplate\img) / 2 + 85)
					
					SetFont I_Opt\Fonts[3]
					
					Local NavWorks% = True
					If PlayerRoom\RoomTemplate\Name$ = "pocketdimension" Lor PlayerRoom\RoomTemplate\Name$ = "dimension1499" Then
						NavWorks% = False
					ElseIf PlayerRoom\RoomTemplate\Name$ = "room860" Then
						For e.Events = Each Events
							If e\EventName = "room860" Then
								If e\EventState = 1.0 Then
									NavWorks% = False
								EndIf
								Exit
							EndIf
						Next
					EndIf
					
					If (Not NavWorks) Then
						If (MilliSecs() Mod 1000) > 300 Then
							Color(200, 0, 0)
							Text(x, y + NAV_HEIGHT / 2 - 80, GetLocalString("Game", "nave06"), True)
							Text(x, y + NAV_HEIGHT / 2 - 60, GetLocalString("Game", "navloc"), True)						
						EndIf
					Else
						
						If (SelectedItem\state > 0 Lor SelectedItem\itemtemplate\tempname = "nav310") And (Rnd(CoffinDistance + 15.0) > 1.0 Lor PlayerRoom\RoomTemplate\Name <> "room895") Then
							
							PlayerX% = Floor((EntityX(PlayerRoom\obj)+8) / 8.0 + 0.5)
							PlayerZ% = Floor((EntityZ(PlayerRoom\obj)+8) / 8.0 + 0.5)
							
							SetBuffer ImageBuffer(NavBG)
							Local xx = x-ImageWidth(SelectedItem\itemtemplate\img)/2
							Local yy = y-ImageHeight(SelectedItem\itemtemplate\img)/2+85
							DrawImage(SelectedItem\itemtemplate\img, xx, yy)
							
							If SelectedItem\itemtemplate\tempname = "badnav" Then
								Local drawx2% = I_Opt\GraphicWidth - ImageWidth(SelectedItem\itemtemplate\img)*0.5+20, drawy2% = I_Opt\GraphicHeight - ImageHeight(SelectedItem\itemtemplate\img)*0.4-85
								
								PlayerX = PlayerX - 1
								PlayerZ = PlayerZ - 1
								
								If PlayerX+1>MapWidth Lor MapTemp(PlayerX+1,PlayerZ)=False Then
									DrawImage NavImages[3],drawx2-12,drawy2-12
								EndIf
								If PlayerX-1<0 Lor MapTemp(PlayerX-1,PlayerZ)=False Then
									DrawImage NavImages[1],drawx2-12,drawy2-12
								EndIf
								If PlayerZ-1<0 Lor MapTemp(PlayerX,PlayerZ-1)=False Then
									DrawImage NavImages[0],drawx2-12,drawy2-12
								EndIf
								If PlayerZ+1>MapHeight Lor MapTemp(PlayerX,PlayerZ+1)=False Then
									DrawImage NavImages[2],drawx2-12,drawy2-12
								EndIf
							ElseIf SelectedItem\itemtemplate\tempname <> "nav"
								x = x - 12 + (((EntityX(Collider)-4.0)+8.0) Mod 8.0)*3
								y = y + 12 - (((EntityZ(Collider)-4.0)+8.0) Mod 8.0)*3
								For x2 = Max(0, PlayerX - 6) To Min(MapWidth, PlayerX + 6)
									For z2 = Max(0, PlayerZ - 6) To Min(MapHeight, PlayerZ + 6)
										If CoffinDistance > 16.0 Lor Rnd(16.0)<CoffinDistance Then 
											If MapTemp(x2, z2)>0 And (MapFound(x2, z2) > 0 Lor SelectedItem\itemtemplate\tempname = "navulti") Then
												Local drawx% = x + (PlayerX - 1 - x2) * 24 , drawy% = y - (PlayerZ - 1 - z2) * 24
												If x2+1>MapWidth Lor MapTemp(x2+1,z2)=False Then
													DrawImage NavImages[3],drawx-12,drawy-12
												EndIf
												If x2-1<0 Lor MapTemp(x2-1,z2)=False Then
													DrawImage NavImages[1],drawx-12,drawy-12
												EndIf
												If z2-1<0 Lor MapTemp(x2,z2-1)=False Then
													DrawImage NavImages[0],drawx-12,drawy-12
												EndIf
												If z2+1>MapHeight Lor MapTemp(x2,z2+1)=False Then
													DrawImage NavImages[2],drawx-12,drawy-12
												EndIf
											EndIf
										EndIf
									Next
								Next
							EndIf
							
							SetBuffer BackBuffer()
							DrawImageRect NavBG,xx+80,yy+70,xx+80,yy+70,270,230
							Color 30,30,30
							Rect xx+80,yy+70,270,230,False
							
							x = I_Opt\GraphicWidth - ImageWidth(SelectedItem\itemtemplate\img)*0.5+20
							y = I_Opt\GraphicHeight - ImageHeight(SelectedItem\itemtemplate\img)*0.4-85
							
							If (MilliSecs() Mod 1000) > 300 Then
								If SelectedItem\itemtemplate\tempname <> "navulti" Then
									Text(x - NAV_WIDTH/2 + 10, y - NAV_HEIGHT/2 + 10, GetLocalString("Game", "navoff"))
								EndIf
								
								yawvalue = EntityYaw(Collider)-90
								x1 = x+Cos(yawvalue)*6 : y1 = y-Sin(yawvalue)*6
								x2 = x+Cos(yawvalue-140)*5 : y2 = y-Sin(yawvalue-140)*5				
								x3 = x+Cos(yawvalue+140)*5 : y3 = y-Sin(yawvalue+140)*5
								
								Line x1,y1,x2,y2
								Line x1,y1,x3,y3
								Line x2,y2,x3,y3
							EndIf
							
							Color 100, 0, 0
							If (SelectedItem\itemtemplate\tempname = "navulti" Lor SelectedItem\itemtemplate\tempname = "nav") And (MilliSecs() Mod 600) < 400 Then
								Local dist#
								Local SCPs_found% = 0
								If Curr173<>Null Then
									dist# = EntityDistanceSquared(Camera, Curr173\obj)
									If dist < 8.0 * 4.0 Then
										dist = Sqr(Ceil(dist / 8.0) * 8.0) ;This is probably done to disguise 173's teleporting behavior
										Oval(x - dist * 3, y - 7 - dist * 3, dist * 3 * 2, dist * 3 * 2, False)
										Text(x - NAV_WIDTH / 2 + 10, y - NAV_HEIGHT / 2 + 30, "SCP-173")
										SCPs_found% = SCPs_found% + 1
									EndIf
								EndIf
								If Curr106<>Null Then
									dist# = EntityDistanceSquared(Camera, Curr106\obj)
									If dist < PowTwo(8.0 * 4.0) Then
										dist = Sqr(dist)
										Oval(x - dist * 1.5, y - 7 - dist * 1.5, dist * 3, dist * 3, False)
										Text(x - NAV_WIDTH / 2 + 10, y - NAV_HEIGHT / 2 + 30 + (20*SCPs_found), "SCP-106")
										SCPs_found% = SCPs_found% + 1
									EndIf
								EndIf
								If Curr096<>Null Then 
									dist# = EntityDistanceSquared(Camera, Curr096\obj)
									If dist < PowTwo(8.0 * 4.0) Then
										dist = Sqr(dist)
										Oval(x - dist * 1.5, y - 7 - dist * 1.5, dist * 3, dist * 3, False)
										Text(x - NAV_WIDTH / 2 + 10, y - NAV_HEIGHT / 2 + 30 + (20*SCPs_found), "SCP-096")
										SCPs_found% = SCPs_found% + 1
									EndIf
								EndIf
								For np.NPCs = Each NPCs ;TODO make Curr049
									If np\NPCtype = NPCtype049
										dist# = EntityDistanceSquared(Camera, np\obj)
										If dist < PowTwo(8.0 * 4.0) Then
											dist = Sqr(dist)
											If (Not np\HideFromNVG) Then
												Oval(x - dist * 1.5, y - 7 - dist * 1.5, dist * 3, dist * 3, False)
												Text(x - NAV_WIDTH / 2 + 10, y - NAV_HEIGHT / 2 + 30 + (20*SCPs_found), "SCP-049")
												SCPs_found% = SCPs_found% + 1
											EndIf
										EndIf
										Exit
									EndIf
								Next
								If PlayerRoom\RoomTemplate\Name = "room895" Then
									If CoffinDistance < 8.0 Then
										dist = Rnd(4.0, 8.0)
										Oval(x - dist * 1.5, y - 7 - dist * 1.5, dist * 3, dist * 3, False)
										Text(x - NAV_WIDTH / 2 + 10, y - NAV_HEIGHT / 2 + 30 + (20*SCPs_found), "SCP-895")
									EndIf
								EndIf
							EndIf
							
							Color (30,30,30)
							If SelectedItem\itemtemplate\tempname <> "nav310" Then
								xtemp = x - NAV_WIDTH/2 + 196
								ytemp = y - NAV_HEIGHT/2 + 10
								Rect xtemp,ytemp,80,20,False
								
								For i = 1 To Min(Ceil(SelectedItem\state / 10.0), 10)
									DrawImage NavImages[4],xtemp+i*8-6,ytemp+4
								Next
								
							EndIf
						EndIf
						
					EndIf

				Case "bad1499","scp1499","super1499","fine1499"
					
					If CanUseItem(False, False) Then
						If SelectedItem\Picked = 2 Lor Wearing1499 = 0 Then
							
							CurrSpeed = CurveValue(0, CurrSpeed, 5.0)
							
							DrawImage(SelectedItem\itemtemplate\invimg, I_Opt\GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, I_Opt\GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
							
							;width = 300
							;height = 20
							x% = I_Opt\GraphicWidth / 2 - 300 / 2
							y% = I_Opt\GraphicHeight / 2 + 80
							Rect(x, y, 300+4, 20, False)
							For  i% = 1 To Int((300 - 2) * (SelectedItem\state / 100.0) / 10)
								DrawImage(BlinkMeterIMG, x + 3 + 10 * (i - 1), y + 3)
							Next
							
							If SelectedItem\itemtemplate\tempname = "fine1499" Then
								SelectedItem\state = Min(SelectedItem\state+(FPSfactor*2),100)
							ElseIf SelectedItem\itemtemplate\tempname = "bad1499"
								SelectedItem\state = Min(SelectedItem\state+(FPSfactor*0.5),100)
							Else
								SelectedItem\state = Min(SelectedItem\state+(FPSfactor),100)
							EndIf
							
							If SelectedItem\state=100 Then
								If Wearing1499<>0 Then
									Wearing1499 = 0
									If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
									SelectedItem\Picked = 1
								Else
									If SelectedItem\itemtemplate\tempname="bad1499" Then
										Wearing1499 = -1
									ElseIf SelectedItem\itemtemplate\tempname="scp1499"
										Wearing1499 = 1
									ElseIf SelectedItem\itemtemplate\tempname = "super1499"
										Wearing1499 = 2
									Else
										Wearing1499 = 3
									EndIf
									If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
									GiveAchievement(Achv1499)
									If WearingNightVision Then CameraFogFar = StoredCameraFogFar
									WearingGasMask = 0
									WearingNightVision = 0
									SelectedItem\Picked = 2
									For r.Rooms = Each Rooms
										If r\RoomTemplate\Name = "dimension1499" Then
											BlinkTimer = -1
											NTF_1499PrevRoom = PlayerRoom
											NTF_1499PrevX# = EntityX(Collider)
											NTF_1499PrevY# = EntityY(Collider)
											NTF_1499PrevZ# = EntityZ(Collider)
											
											If NTF_1499X# = 0.0 And NTF_1499Y# = 0.0 And NTF_1499Z# = 0.0 Then
												PositionEntity (Collider, r\x+6086.0*RoomScale, r\y+304.0*RoomScale, r\z+2292.5*RoomScale)
												RotateEntity Collider,0,90,0,True
											Else
												PositionEntity (Collider, NTF_1499X#, NTF_1499Y#+0.05, NTF_1499Z#)
											EndIf
											ResetEntity(Collider)
											UpdateDoors()
											UpdateRooms()
											For it.Items = Each Items
												it\disttimer = 0
											Next
											PlayerRoom = r
											PlaySound_Strict (LoadTempSound("SFX\SCP\1499\Enter.ogg"))
											NTF_1499X# = 0.0
											NTF_1499Y# = 0.0
											NTF_1499Z# = 0.0
											If Curr096<>Null Then
												If Curr096\SoundChn<>0 Then
													SetStreamVolume_Strict(Curr096\SoundChn,0.0)
												EndIf
											EndIf
											For e.Events = Each Events
												If e\EventName = "dimension1499" Then
													If EntityDistanceSquared(e\room\obj,Collider) > PowTwo(8300.0*RoomScale) Then
														If e\EventState2 < 5 Then
															e\EventState2 = e\EventState2 + 1
														EndIf
													EndIf
													Exit
												EndIf
											Next
											Exit
										EndIf
									Next
								EndIf
								SelectedItem\state=0
								SelectedItem = Null
							EndIf
							
						Else
							Msg = GetLocalString("Messages", "1499double")
							MsgTimer = 70 * 10
						EndIf
					EndIf
				Case "badge"
					If SelectedItem\itemtemplate\img=0 Then
						SelectedItem\itemtemplate\img=LoadImage_Strict(SelectedItem\itemtemplate\imgpath)
						MaskImage(SelectedItem\itemtemplate\img, 255, 0, 255)
					EndIf
					
					DrawImage(SelectedItem\itemtemplate\img, I_Opt\GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\img) / 2, I_Opt\GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\img) / 2)
					
					If SelectedItem\state = 0 Then
						PlaySound_Strict LoadTempSound("SFX\SCP\1162\NostalgiaCancer"+Rand(6,10)+".ogg")
						Select SelectedItem\itemtemplate\namespec
							Case "oldbadge"
								Msg = GetLocalString("Messages", "nostalgiabadge")
								MsgTimer = 70*10
						End Select
						
						SelectedItem\state = 1
					EndIf
				Case "key"
					If SelectedItem\state = 0 Then
						PlaySound_Strict LoadTempSound("SFX\SCP\1162\NostalgiaCancer"+Rand(6,10)+".ogg")
						
						Msg = GetLocalString("Messages", "nostalgiakey")
						MsgTimer = 70*10			
						SelectedItem\state = 1
					EndIf
					
					SelectedItem = Null
				Case "coin"
					If SelectedItem\state = 0
						PlaySound_Strict LoadTempSound("SFX\SCP\1162\NostalgiaCancer"+Rand(1,5)+".ogg")
						Msg = GetLocalString("Messages", "nostalgiacoin")
						MsgTimer = 70*10
						SelectedItem\state = 1
					EndIf
					
					DrawImage(SelectedItem\itemtemplate\invimg, I_Opt\GraphicWidth / 2 - ImageWidth(SelectedItem\itemtemplate\invimg) / 2, I_Opt\GraphicHeight / 2 - ImageHeight(SelectedItem\itemtemplate\invimg) / 2)
				Case "scp427"
					
					If SelectedItem\Picked = 2 Then
						Msg = GetLocalString("Messages", "427close")
						I_427\Using = 0
						SelectedItem\Picked = 1
					ElseIf I_427\Using>0
						Msg = GetLocalString("Messages", "427double")
					Else
						GiveAchievement(Achv427)
						Msg = GetLocalString("Messages", "427open")
						I_427\Using = 1
						SelectedItem\Picked = 2
					EndIf
					MsgTimer = 70 * 5
					SelectedItem = Null
					
				Case "super427"
					
					If SelectedItem\Picked = 2 Then
						Msg = GetLocalString("Messages", "427close")
						I_427\Using = 0
						SelectedItem\Picked = 1
					ElseIf I_427\Using>0
						Msg = GetLocalString("Messages", "427double")
					Else
						GiveAchievement(Achv427)
						Msg = GetLocalString("Messages", "427open")
						I_427\Using = 2
						SelectedItem\Picked = 2
					EndIf
					MsgTimer = 70 * 5
					SelectedItem = Null

				Case "pill"
					
					If CanUseItem(False, True)
						Msg = GetLocalString("Messages", "pillswallow")
						MsgTimer = 70*7
						
						RemoveItem(SelectedItem)
						SelectedItem = Null
					EndIf	

				Case "upgradedpill"

					If CanUseItem(False, True)
						Msg = GetLocalString("Messages", "pillswallow")
						MsgTimer = 70*7
						
						If I_427\Timer < 70*360 Then
							I_427\Timer = 70*360
						EndIf
						
						RemoveItem(SelectedItem)
						SelectedItem = Null
					EndIf

				Default
					; check if the item is an inventory-type object
					If SelectedItem\invSlots>0 Then
						DoubleClick = 0
						;MouseHit1 = 0
						MouseDown1 = 0
						LastMouseHit1 = 0
						OtherOpen = SelectedItem
					EndIf
					
					SelectedItem = Null
			End Select
			
			If SelectedItem <> Null Then
				If SelectedItem\itemtemplate\img <> 0
					Local IN$ = SelectedItem\itemtemplate\tempname
					If IN$ = "paper" Lor IN$ = "badge" Lor IN$ = "ticket" Then
						For a_it.Items = Each Items
							If a_it <> SelectedItem
								Local IN2$ = a_it\itemtemplate\tempname
								If IN2$ = "paper" Lor IN2$ = "badge" Lor IN2$ = "ticket" Then
									If a_it\itemtemplate\img<>0
										If a_it\itemtemplate\img <> SelectedItem\itemtemplate\img
											FreeImage(a_it\itemtemplate\img)
											a_it\itemtemplate\img = 0
										EndIf
									EndIf
								EndIf
							EndIf
						Next
					EndIf
				EndIf			
			EndIf
			
			If MouseHit2 Then
				
				EntityAlpha Dark, 0.0
				
				IN$ = SelectedItem\itemtemplate\tempname
				If IN = "scp1025" Then
					If SelectedItem\itemtemplate\img<>0 Then FreeImage(SelectedItem\itemtemplate\img)
					SelectedItem\itemtemplate\img=0
				ElseIf IN = "firstaid" Lor IN$="finefirstaid" Lor IN$="firstaid2" Then
					SelectedItem\state = 0
				ElseIf IN = "badvest" Lor IN = "vest" Lor IN="finevest"
					SelectedItem\state = 0
					If (WearingVest = 0)
						DropItem(SelectedItem,False)
					EndIf
				ElseIf IN = "hazmat0" Lor IN = "hazmat" Lor IN="hazmat2" Lor IN="hazmat3"
					SelectedItem\state = 0
					If WearingHazmat <> 1
						DropItem(SelectedItem,False)
					EndIf
				ElseIf IN = "bad1499" Lor IN$="scp1499" Lor IN$="super1499" Lor IN$="fine1499"
					SelectedItem\state = 0
				EndIf
				
				If SelectedItem\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(SelectedItem\itemtemplate\sound))
				SelectedItem = Null
			EndIf
		EndIf		
	EndIf
	
	If SelectedItem = Null Then
		For i = 0 To 6
			If RadioCHN[i] <> 0 Then 
				If ChannelPlaying(RadioCHN[i]) Then PauseChannel(RadioCHN[i])
			EndIf
		Next
	EndIf
	
	For it.Items = Each Items
		If it<>SelectedItem
			Select it\itemtemplate\tempname
				Case "firstaid", "finefirstaid", "firstaid2", "badvest", "vest", "finevest", "hazmat0", "hazmat", "hazmat2", "hazmat3", "bad1499", "scp1499", "super1499", "fine1499"
					it\state = 0
			End Select
		EndIf
	Next
	
	If PrevInvOpen And (Not InvOpen) Then MoveMouse viewport_center_x, viewport_center_y
	
	;CatchErrors("Uncaught DrawGUI")
End Function

Function DrawMenu()
	;CatchErrors("DrawMenu")
	
	Local x%, y%, width%, height%
	If InFocus() = 0 Then ;Game is out of focus -> pause the game
		If (Not Using294) Then
			MenuOpen = True
			PauseSounds()
		EndIf
		Delay 1000 ;Reduce the CPU take while game is not in focus
	EndIf
	If MenuOpen Then
		
		;DebugLog AchievementsMenu+"|"+OptionsMenu+"|"+QuitMSG
		
		If (PlayerRoom\RoomTemplate\Name$ <> "gateb" And EntityY(Collider) =< 1040.0 * RoomScale) And PlayerRoom\RoomTemplate\Name$ <> "gatea"
			If StopHidingTimer = 0 Then
				If EntityDistanceSquared(Curr173\Collider, Collider) < PowTwo(4.0) Lor EntityDistanceSquared(Curr106\Collider, Collider) < PowTwo(4.0) Then
					StopHidingTimer = 1
				EndIf	
			ElseIf StopHidingTimer < 40
				If KillTimer >= 0 Then 
					StopHidingTimer = StopHidingTimer+FPSfactor
					
					If StopHidingTimer => 40 Then
						PlaySound_Strict(HorrorSFX(15))
						Msg = GetLocalString("Menu", "stophide")
						MsgTimer = 6*70
						MenuOpen = False
						Return
					EndIf
				EndIf
			EndIf
		EndIf
		
		InvOpen = False
		
		width = ImageWidth(PauseMenuIMG)
		height = ImageHeight(PauseMenuIMG)
		x = I_Opt\GraphicWidth / 2 - width / 2
		y = I_Opt\GraphicHeight / 2 - height / 2
		
		DrawImage PauseMenuIMG, x, y
		
		Color(255, 255, 255)
		
		x = x+132*MenuScale
		y = y+122*MenuScale	
		
		If (Not MouseDown1)
			OnSliderID = 0
		EndIf
		
		If AchievementsMenu > 0 Then
			SetFont I_Opt\Fonts[2]
			Text(x, y-(122-45)*MenuScale, Upper(GetLocalString("Menu", "ach")),False,True)
			SetFont I_Opt\Fonts[1]
		ElseIf OptionsMenu > 0 Then
			SetFont I_Opt\Fonts[2]
			Text(x, y-(122-45)*MenuScale, Upper(GetLocalString("Menu", "options")),False,True)
			SetFont I_Opt\Fonts[1]
		ElseIf QuitMSG > 0 Then
			SetFont I_Opt\Fonts[2]
			Text(x, y-(122-45)*MenuScale, Upper(GetLocalString("Menu", "quit")) + "?",False,True)
			SetFont I_Opt\Fonts[1]
		ElseIf KillTimer >= 0 Then
			SetFont I_Opt\Fonts[2]
			Text(x, y-(122-45)*MenuScale, GetLocalString("Menu", "paused"),False,True)
			SetFont I_Opt\Fonts[1]
		Else
			SetFont I_Opt\Fonts[2]
			Text(x, y-(122-45)*MenuScale, GetLocalString("Menu", "died"),False,True)
			SetFont I_Opt\Fonts[1]
		EndIf		
		
		Local AchvXIMG% = (x + (22*MenuScale))
		Local scale# = I_Opt\GraphicHeight/768.0
		Local SeparationConst% = 76*scale
		Local imgsize% = 64
		
		If AchievementsMenu <= 0 And OptionsMenu <= 0 And QuitMSG <= 0
			SetFont I_Opt\Fonts[1]
			Text x, y, GetLocalString("Menu", "difficulty")+": "+SelectedDifficulty\name
			If CurrSave = Null Then
				Text x, y+20*MenuScale, GetLocalString("Menu", "save")+": "+GetLocalString("Deaths", "redacted")
			Else
				Text x, y+20*MenuScale, GetLocalString("Menu", "save")+": "+CurrSave\Name
			EndIf
			Text x, y+40*MenuScale, GetLocalString("Menu", "seed")+": "+RandomSeed
		ElseIf AchievementsMenu <= 0 And OptionsMenu > 0 And QuitMSG <= 0 And KillTimer >= 0
			If DrawButton(x + 101 * MenuScale, y + 390 * MenuScale, 230 * MenuScale, 60 * MenuScale, GetLocalString("Menu", "back")) Then
				AchievementsMenu = 0
				OptionsMenu = 0
				QuitMSG = 0
				MouseHit1 = False
				SaveOptionsINI()
				
				AntiAlias Opt_AntiAlias
				TextureLodBias TextureFloat#
			EndIf
			
			Color 0,255,0
			If OptionsMenu = 1
				Rect(x-10*MenuScale,y-5*MenuScale,110*MenuScale,40*MenuScale,True)
			ElseIf OptionsMenu = 2
				Rect(x+100*MenuScale,y-5*MenuScale,110*MenuScale,40*MenuScale,True)
			ElseIf OptionsMenu = 3
				Rect(x+210*MenuScale,y-5*MenuScale,110*MenuScale,40*MenuScale,True)
			ElseIf OptionsMenu = 4
				Rect(x+320*MenuScale,y-5*MenuScale,110*MenuScale,40*MenuScale,True)
			EndIf
			
			If DrawButton(x-5*MenuScale,y,100*MenuScale,30*MenuScale,GetLocalString("Options", "graphics"),False) Then OptionsMenu = 1
			If DrawButton(x+105*MenuScale,y,100*MenuScale,30*MenuScale,GetLocalString("Options", "audio"),False) Then OptionsMenu = 2
			If DrawButton(x+215*MenuScale,y,100*MenuScale,30*MenuScale,GetLocalString("Options", "control"),False) Then OptionsMenu = 3
			If DrawButton(x+325*MenuScale,y,100*MenuScale,30*MenuScale,GetLocalString("Options", "advanced"),False) Then OptionsMenu = 4
			
			Local tx# = (I_Opt\GraphicWidth/2)+(width/2)
			Local ty# = y
			Local tw# = 400*MenuScale
			Local th# = 150*MenuScale
			
			Color 255,255,255
			Select OptionsMenu
				Case 1 ;Graphics
					SetFont I_Opt\Fonts[1]

					y=y+50*MenuScale
					
					Color 100,100,100
					Text(x, y, GetLocalString("Options", "bumpmap"))	
					BumpEnabled = DrawTick(x + 270 * MenuScale, y + MenuScale, BumpEnabled, True)
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20*MenuScale,20*MenuScale) And OnSliderID=0
						DrawOptionsTooltip(tx,ty,tw,th,"bump")
					EndIf
					
					y=y+30*MenuScale
					
					Color 255,255,255
					Text(x, y, GetLocalString("Options", "vsync"))
					Vsync% = DrawTick(x + 270 * MenuScale, y + MenuScale, Vsync%)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale) And OnSliderID=0
						DrawOptionsTooltip(tx,ty,tw,th,"vsync")
					EndIf
					
					y=y+30*MenuScale
					
					Color 255,255,255
					Text(x, y, GetLocalString("Options", "antialias"))
					Opt_AntiAlias = DrawTick(x + 270 * MenuScale, y + MenuScale, Opt_AntiAlias%)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale) And OnSliderID=0
						DrawOptionsTooltip(tx,ty,tw,th,"antialias")
					EndIf
					
					y=y+30*MenuScale
					
					Color 255,255,255
					Text(x, y, GetLocalString("Options", "roomlights"))
					EnableRoomLights = DrawTick(x + 270 * MenuScale, y + MenuScale, EnableRoomLights)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale) And OnSliderID=0
						DrawOptionsTooltip(tx,ty,tw,th,"roomlights")
					EndIf
					
					y=y+30*MenuScale
					
					Color 100,100,100
					Text(x, y, GetLocalString("Options", "vram"))	
					EnableVRam = DrawTick(x + 270 * MenuScale, y + MenuScale, EnableVRam, True)
					If MouseOn(x + 270 * MenuScale, y + MenuScale, 20*MenuScale,20*MenuScale) And OnSliderID=0
						DrawOptionsTooltip(tx,ty,tw,th,"vram")
					EndIf
					
					y=y+30*MenuScale
					
					ScreenGamma = (SlideBar(x + 270*MenuScale, y+6*MenuScale, 100*MenuScale, ScreenGamma*50.0)/50.0)
					Color 255,255,255
					Text(x, y, GetLocalString("Options", "gamma"))
					If MouseOn(x+270*MenuScale,y+6*MenuScale,100*MenuScale+14,20) And OnSliderID=0
						DrawOptionsTooltip(tx,ty,tw,th,"gamma",ScreenGamma)
					EndIf
					
					;y = y + 50*MenuScale
					
					y=y+50*MenuScale
					
					Color 255,255,255
					Text(x, y, GetLocalString("Options", "pamount"))
					ParticleAmount = Slider3(x+270*MenuScale,y+6*MenuScale,100*MenuScale,ParticleAmount,2,"MINIMAL","REDUCED","FULL")
					If OnSliderID=2 Lor (MouseOn(x + 270 * MenuScale, y-6*MenuScale, 100*MenuScale+14, 20) And OnSliderID=0)
						DrawOptionsTooltip(tx,ty,tw,th,"particleamount",ParticleAmount)
					EndIf
					
					y=y+50*MenuScale
					
					Color 255,255,255
					Text(x, y, GetLocalString("Options", "lod"))
					TextureDetails = Slider5(x+270*MenuScale,y+6*MenuScale,100*MenuScale,TextureDetails,3,"0.8","0.4","0.0","-0.4","-0.8")
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
					If OnSliderID=3 Lor (MouseOn(x+270*MenuScale,y-6*MenuScale,100*MenuScale+14,20) And OnSliderID=0)
						DrawOptionsTooltip(tx,ty,tw,th+100*MenuScale,"texquality")
					EndIf
					
					y=y+50*MenuScale
					
					Local SlideBarFOV% = FOV-40
					SlideBarFOV = (SlideBar(x + 270*MenuScale, y+6*MenuScale,100*MenuScale, SlideBarFOV*2.0)/2.0)
					FOV = SlideBarFOV+40
					Color 255,255,255
					Text(x, y, GetLocalString("Options", "fov"))
					Color 255,255,0
					Text(x + 5 * MenuScale, y + 25 * MenuScale, FOV + " FOV")
					If MouseOn(x+270*MenuScale,y+6*MenuScale,100*MenuScale+14,20)
						DrawOptionsTooltip(tx,ty,tw,th,"fov")
					EndIf
					
					CameraZoom(Camera, Min(1.0+(CurrCameraZoom/400.0),1.1) / Tan((2*ATan(Tan(Float(FOV * (InvertCam*-2+1))/2)*RealGraphicWidth/RealGraphicHeight))/2.0))

				Case 2 ;Audio
					SetFont I_Opt\Fonts[1]

					y = y + 50*MenuScale
					
					MusicVolume = (SlideBar(x + 250*MenuScale, y-4*MenuScale, 100*MenuScale, MusicVolume*100.0)/100.0)
					Color 255,255,255
					Text(x, y, GetLocalString("Options", "musicv"))
					If MouseOn(x+250*MenuScale,y-4*MenuScale,100*MenuScale+14,20)
						DrawOptionsTooltip(tx,ty,tw,th,"musicvol",MusicVolume)
					EndIf
					
					y = y + 30*MenuScale
					
					PrevSFXVolume = (SlideBar(x + 250*MenuScale, y-4*MenuScale, 100*MenuScale, SFXVolume*100.0)/100.0)
					If (Not DeafPlayer) Then SFXVolume# = PrevSFXVolume#
					Color 255,255,255
					Text(x, y, GetLocalString("Options", "soundv"))
					If MouseOn(x+250*MenuScale,y-4*MenuScale,100*MenuScale+14,20)
						DrawOptionsTooltip(tx,ty,tw,th,"soundvol",PrevSFXVolume)
					EndIf
					
					y = y + 30*MenuScale
					
					Color 100,100,100
					Text x, y, GetLocalString("Options", "soundautor")
					EnableSFXRelease = DrawTick(x + 270 * MenuScale, y + MenuScale, EnableSFXRelease,True)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th+220*MenuScale,"sfxautorelease")
					EndIf
					
					y = y + 30*MenuScale
					
					Color 100,100,100
					Text x, y, GetLocalString("Options", "usertracks")
					EnableUserTracks = DrawTick(x + 270 * MenuScale, y + MenuScale, EnableUserTracks,True)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"usertrack")
					EndIf
					
					If EnableUserTracks
						y = y + 30 * MenuScale
						Color 255,255,255
						Text x, y, GetLocalString("Options", "usertrackm")
						UserTrackMode = DrawTick(x + 270 * MenuScale, y + MenuScale, UserTrackMode)
						If UserTrackMode
							Text x, y + 20 * MenuScale, GetLocalString("Options", "usertrackrepeat")
						Else
							Text x, y + 20 * MenuScale, GetLocalString("Options", "usertrackrandom")
						EndIf
						If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
							DrawOptionsTooltip(tx,ty,tw,th,"usertrackmode")
						EndIf
						;DrawButton(x, y + 30 * MenuScale, 190 * MenuScale, 25 * MenuScale, GetLocalString("Options", "usertrackscan"),False)
						;If MouseOn(x,y+30*MenuScale,190*MenuScale,25*MenuScale)
						;	DrawOptionsTooltip(tx,ty,tw,th,"usertrackscan")
						;EndIf
					EndIf

				Case 3 ;Controls
					SetFont I_Opt\Fonts[1]

					y = y + 50*MenuScale
					
					MouseSens = (SlideBar(x + 270*MenuScale, y-4*MenuScale, 100*MenuScale, (MouseSens+0.5)*100.0)/100.0)-0.5
					Color(255, 255, 255)
					Text(x, y, GetLocalString("Options", "sensitivity"))
					If MouseOn(x+270*MenuScale,y-4*MenuScale,100*MenuScale+14,20)
						DrawOptionsTooltip(tx,ty,tw,th,"mousesensitivity",MouseSens)
					EndIf
					
					y = y + 30*MenuScale
					
					Color(255, 255, 255)
					Text(x, y, GetLocalString("Options", "invert"))
					InvertMouse = DrawTick(x + 270 * MenuScale, y + MenuScale, InvertMouse)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"mouseinvert")
					EndIf
					
					y = y + 40*MenuScale
					
					MouseSmooth = (SlideBar(x + 270*MenuScale, y-4*MenuScale, 100*MenuScale, (MouseSmooth)*50.0)/50.0)
					Color(255, 255, 255)
					Text(x, y, GetLocalString("Options", "smooth"))
					If MouseOn(x+270*MenuScale,y-4*MenuScale,100*MenuScale+14,20)
						DrawOptionsTooltip(tx,ty,tw,th,"mousesmoothing",MouseSmooth)
					EndIf
					
					Color(255, 255, 255)
					
					y = y + 30*MenuScale
					Text(x, y, GetLocalString("Options", "keys"))
					y = y + 10*MenuScale
					
					Text(x, y + 20 * MenuScale, GetLocalString("Options", "forward"))
					InputBox(x + 200 * MenuScale, y + 20 * MenuScale,100*MenuScale,20*MenuScale,I_Keys\KeyName[Min(I_Keys\UP,210)],5)		
					Text(x, y + 40 * MenuScale, GetLocalString("Options", "left"))
					InputBox(x + 200 * MenuScale, y + 40 * MenuScale,100*MenuScale,20*MenuScale,I_Keys\KeyName[Min(I_Keys\Left,210)],3)	
					Text(x, y + 60 * MenuScale, GetLocalString("Options", "backward"))
					InputBox(x + 200 * MenuScale, y + 60 * MenuScale,100*MenuScale,20*MenuScale,I_Keys\KeyName[Min(I_Keys\DOWN,210)],6)				
					Text(x, y + 80 * MenuScale, GetLocalString("Options", "right"))
					InputBox(x + 200 * MenuScale, y + 80 * MenuScale,100*MenuScale,20*MenuScale,I_Keys\KeyName[Min(I_Keys\Right,210)],4)
					
					Text(x, y + 100 * MenuScale, GetLocalString("Options", "blink"))
					InputBox(x + 200 * MenuScale, y + 100 * MenuScale,100*MenuScale,20*MenuScale,I_Keys\KeyName[Min(I_Keys\BLINK,210)],7)				
					Text(x, y + 120 * MenuScale, GetLocalString("Options", "sprint"))
					InputBox(x + 200 * MenuScale, y + 120 * MenuScale,100*MenuScale,20*MenuScale,I_Keys\KeyName[Min(I_Keys\SPRINT,210)],8)
					Text(x, y + 140 * MenuScale, GetLocalString("Options", "inv"))
					InputBox(x + 200 * MenuScale, y + 140 * MenuScale,100*MenuScale,20*MenuScale,I_Keys\KeyName[Min(I_Keys\INV,210)],9)
					Text(x, y + 160 * MenuScale, GetLocalString("Options", "crouch"))
					InputBox(x + 200 * MenuScale, y + 160 * MenuScale,100*MenuScale,20*MenuScale,I_Keys\KeyName[Min(I_Keys\CROUCH,210)],10)
					Text(x, y + 180 * MenuScale, GetLocalString("Options", "save"))
					InputBox(x + 200 * MenuScale, y + 180 * MenuScale,100*MenuScale,20*MenuScale,I_Keys\KeyName[Min(I_Keys\SAVE,210)],11)	
					Text(x, y + 200 * MenuScale, GetLocalString("Options", "console"))
					InputBox(x + 200 * MenuScale, y + 200 * MenuScale,100*MenuScale,20*MenuScale,I_Keys\KeyName[Min(I_Keys\CONSOLE,210)],12)
					
					If MouseOn(x,y,300*MenuScale,220*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"controls")
					EndIf
					
					For i = 0 To 227
						If KeyHit(i) Then key = i : Exit
					Next
					If key <> 0 Then
						Select SelectedInputBox
							Case 3
								I_Keys\Left = key
							Case 4
								I_Keys\Right = key
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

				Case 4 ;Advanced
					SetFont I_Opt\Fonts[1]
					
					Local PrevFramelimit% = Framelimit

					y = y + 50*MenuScale
					
					Color 255,255,255				
					Text(x, y, GetLocalString("Options", "hud"))	
					HUDenabled = DrawTick(x + 270 * MenuScale, y + MenuScale, HUDenabled)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"hud")
					EndIf
					
					y = y + 30*MenuScale
					
					Color 255,255,255
					Text(x, y, GetLocalString("Options", "aconsole"))
					I_Opt\ConsoleEnabled = DrawTick(x +270 * MenuScale, y + MenuScale, I_Opt\ConsoleEnabled)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"consoleenable")
					EndIf
					
					y = y + 30*MenuScale
					
					Color 255,255,255
					Text(x, y, GetLocalString("Options", "errconsole"))
					I_Opt\ConsoleOnError = DrawTick(x + 270 * MenuScale, y + MenuScale, I_Opt\ConsoleOnError)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"consoleerror")
					EndIf
					
					y = y + 50*MenuScale
					
					Color 255,255,255
					Text(x, y, GetLocalString("Options", "achpop"))
					AchvMSGenabled% = DrawTick(x + 270 * MenuScale, y, AchvMSGenabled%)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"achpopup")
					EndIf
					
					y = y + 50*MenuScale
					
					Color 255,255,255
					Text(x, y, GetLocalString("Options", "fps"))
					I_Opt\ShowFPS% = DrawTick(x + 270 * MenuScale, y, I_Opt\ShowFPS%)
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale)
						DrawOptionsTooltip(tx,ty,tw,th,"showfps")
					EndIf
					
					y = y + 30*MenuScale
					
					Color 255,255,255
					Text(x, y, GetLocalString("Options", "fpslimit"))
					
					Color 255,255,255
					If DrawTick(x + 270 * MenuScale, y, PrevFramelimit > 0) Then
						If PrevFramelimit > 0 Then
							Framelimit = 20+(SlideBar(x + 150*MenuScale, y+30*MenuScale, 100*MenuScale, Framelimit-20))
							Color 255,255,0
							Text(x + 5 * MenuScale, y + 25 * MenuScale, Framelimit+" FPS")
						Else
							Framelimit = 60
						EndIf
					Else
						Framelimit = 0
					EndIf
					If MouseOn(x+270*MenuScale,y+MenuScale,20*MenuScale,20*MenuScale) Lor (MouseOn(x+150*MenuScale,y+30*MenuScale,100*MenuScale+14,20) And PrevFramelimit > 0) Then
						DrawOptionsTooltip(tx,ty,tw,th,"framelimit",PrevFramelimit > 0)
					EndIf
					
					If PrevFramelimit Then
						y = y + 80*MenuScale
					Else
						y = y + 50*MenuScale
					EndIf

			End Select
		ElseIf AchievementsMenu <= 0 And OptionsMenu <= 0 And QuitMSG > 0 And KillTimer >= 0
			Local QuitButton% = 60 
			If SelectedDifficulty\saveType = SAVEONQUIT Lor SelectedDifficulty\saveType = SAVEANYWHERE Then
				Local RN$ = PlayerRoom\RoomTemplate\Name$
				Local AbleToSave% = True
				If RN$ = "room173" Lor RN$ = "dimension1499" Lor RN$ = "gatea" Lor (RN$ = "gateb" And EntityY(Collider)>1040.0*RoomScale) Then AbleToSave = False
				If (Not CanSave) Then AbleToSave = False
				If AbleToSave
					QuitButton = 140
					If DrawButton(x, y + 60*MenuScale, 390*MenuScale, 60*MenuScale, GetLocalString("Menu", "quitsave")) Then
						DropSpeed = 0
						SaveGame(CurrSave\Name)
						NullGame()
						MenuOpen = False
						MainMenuOpen = True
						MainMenuTab = 0
						CurrSave = Null
						FlushKeys()
					EndIf
				EndIf
			EndIf
			
			If DrawButton(x, y + QuitButton*MenuScale, 390*MenuScale, 60*MenuScale, GetLocalString("Menu", "quit")) Then
				NullGame()
				MenuOpen = False
				MainMenuOpen = True
				MainMenuTab = 0
				CurrSave = Null
				FlushKeys()
			EndIf
			
			If DrawButton(x+101*MenuScale, y + 344*MenuScale, 230*MenuScale, 60*MenuScale, GetLocalString("Menu", "back")) Then
				AchievementsMenu = 0
				OptionsMenu = 0
				QuitMSG = 0
				MouseHit1 = False
			EndIf
		Else
			If DrawButton(x+101*MenuScale, y + 344*MenuScale, 230*MenuScale, 60*MenuScale, GetLocalString("Menu", "back")) Then
				AchievementsMenu = 0
				OptionsMenu = 0
				QuitMSG = 0
				MouseHit1 = False
			EndIf
			
			If AchievementsMenu>0 Then
				;DebugLog AchievementsMenu
				If AchievementsMenu <= Floor(Float(MAXACHIEVEMENTS-1)/12.0) Then 
					If DrawButton(x+341*MenuScale, y + 344*MenuScale, 50*MenuScale, 60*MenuScale, ">") Then
						AchievementsMenu = AchievementsMenu+1
					EndIf
				EndIf
				If AchievementsMenu > 1 Then
					If DrawButton(x+41*MenuScale, y + 344*MenuScale, 50*MenuScale, 60*MenuScale, "<") Then
						AchievementsMenu = AchievementsMenu-1
					EndIf
				EndIf
				
				For i=0 To 11
					If i+((AchievementsMenu-1)*12)<MAXACHIEVEMENTS Then
						DrawAchvIMG(AchvXIMG,y+((i/4)*120*MenuScale),i+((AchievementsMenu-1)*12))
					Else
						Exit
					EndIf
				Next
				
				For i=0 To 11
					If i+((AchievementsMenu-1)*12)<MAXACHIEVEMENTS Then
						If MouseOn(AchvXIMG+((i Mod 4)*SeparationConst),y+((i/4)*120*MenuScale),64*scale,64*scale) Then
							AchievementTooltip(i+((AchievementsMenu-1)*12))
							Exit
						EndIf
					Else
						Exit
					EndIf
				Next
				
			EndIf
		EndIf
		
		y = y+10
		
		If AchievementsMenu<=0 And OptionsMenu<=0 And QuitMSG<=0 Then
			If KillTimer >= 0 Then	
				
				y = y+ 72*MenuScale
				
				If DrawButton(x, y, 390*MenuScale, 60*MenuScale, GetLocalString("Menu", "resume"), True, True) Then
					MenuOpen = False
					ResumeSounds()
					MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
				EndIf
				
				y = y + 75*MenuScale
				If (Not SelectedDifficulty\permaDeath) Then
					If GameSaved Then
						If DrawButton(x, y, 390*MenuScale, 60*MenuScale, GetLocalString("Menu", "loadgame")) Then
							DrawLoading(0)
							
							MenuOpen = False
							LoadGameQuick(CurrSave\Name)
							
							MoveMouse viewport_center_x,viewport_center_y
							SetFont I_Opt\Fonts[1]
							HidePointer ()
							
							FlushKeys()
							FlushMouse()
							Playable=True
							
							UpdateRooms()
							
							For r.Rooms = Each Rooms
								x = Abs(EntityX(Collider) - EntityX(r\obj))
								z = Abs(EntityZ(Collider) - EntityZ(r\obj))
								
								If x < 12.0 And z < 12.0 Then
									MapFound(Floor(EntityX(r\obj) / 8.0), Floor(EntityZ(r\obj) / 8.0)) = Max(MapFound(Floor(EntityX(r\obj) / 8.0), Floor(EntityZ(r\obj) / 8.0)), 1)
									If x < 4.0 And z < 4.0 Then
										If Abs(EntityY(Collider) - EntityY(r\obj)) < 1.5 Then PlayerRoom = r
										MapFound(Floor(EntityX(r\obj) / 8.0), Floor(EntityZ(r\obj) / 8.0)) = 1
									EndIf
								EndIf
							Next
							
							DrawLoading(100)
							
							DropSpeed=0
							
							UpdateWorld 0.0
							
							PrevTime = MilliSecs()
							FPSfactor = 0
							
							ResetInput()
						EndIf
					Else
						DrawFrame(x,y,390*MenuScale, 60*MenuScale)
						Color (100, 100, 100)
						SetFont I_Opt\Fonts[2]
						Text(x + (390*MenuScale) / 2, y + (60*MenuScale) / 2, GetLocalString("Menu", "loadgame"), True, True)
					EndIf
					y = y + 75*MenuScale
				EndIf
				If DrawButton(x, y, 390*MenuScale, 60*MenuScale, GetLocalString("Menu", "ach")) Then AchievementsMenu = 1
				y = y + 75*MenuScale
				If DrawButton(x, y, 390*MenuScale, 60*MenuScale, GetLocalString("Menu", "options")) Then OptionsMenu = 1
				y = y + 75*MenuScale
			Else
				y = y+104*MenuScale
				If GameSaved And (Not SelectedDifficulty\permaDeath) Then
					If DrawButton(x, y, 390*MenuScale, 60*MenuScale, GetLocalString("Menu", "loadgame")) Then
						DrawLoading(0)
						
						MenuOpen = False
						LoadGameQuick(CurrSave\Name)
						
						MoveMouse viewport_center_x,viewport_center_y
						SetFont I_Opt\Fonts[1]
						HidePointer ()
						
						FlushKeys()
						FlushMouse()
						Playable=True
						
						UpdateRooms()
						
						For r.Rooms = Each Rooms
							x = Abs(EntityX(Collider) - EntityX(r\obj))
							z = Abs(EntityZ(Collider) - EntityZ(r\obj))
							
							If x < 12.0 And z < 12.0 Then
								MapFound(Floor(EntityX(r\obj) / 8.0), Floor(EntityZ(r\obj) / 8.0)) = Max(MapFound(Floor(EntityX(r\obj) / 8.0), Floor(EntityZ(r\obj) / 8.0)), 1)
								If x < 4.0 And z < 4.0 Then
									If Abs(EntityY(Collider) - EntityY(r\obj)) < 1.5 Then PlayerRoom = r
									MapFound(Floor(EntityX(r\obj) / 8.0), Floor(EntityZ(r\obj) / 8.0)) = 1
								EndIf
							EndIf
						Next
						
						DrawLoading(100)
						
						DropSpeed=0
						
						UpdateWorld 0.0
						
						PrevTime = MilliSecs()
						FPSfactor = 0
						
						ResetInput()
					EndIf
				Else
					DrawButton(x, y, 390*MenuScale, 60*MenuScale, "")
					Color 50,50,50
					Text(x + 185*MenuScale, y + 30*MenuScale, GetLocalString("Menu", "loadgame"), True, True)
				EndIf
				If DrawButton(x, y + 80*MenuScale, 390*MenuScale, 60*MenuScale, GetLocalString("Menu", "quitmenu")) Then
					NullGame()
					MenuOpen = False
					MainMenuOpen = True
					MainMenuTab = 0
					CurrSave = Null
					FlushKeys()
				EndIf
				y= y + 80*MenuScale
			EndIf
			
			If KillTimer >= 0 And (Not MainMenuOpen)
				If DrawButton(x, y, 390*MenuScale, 60*MenuScale, GetLocalString("Menu", "quit")) Then
					QuitMSG = 1
				EndIf
			EndIf
			
			SetFont I_Opt\Fonts[1]
			If KillTimer < 0 Then RowText(DeathMSG$, x, y + 80*MenuScale, 390*MenuScale, 600*MenuScale)
		EndIf

		If I_Opt\GraphicMode = 0 Then DrawImage CursorIMG, ScaledMouseX(),ScaledMouseY()
		
	EndIf
	
	SetFont I_Opt\Fonts[1]
	
	;CatchErrors("Uncaught DrawMenu")
End Function

Function MouseOn%(x%, y%, width%, height%)
	If ScaledMouseX() > x And ScaledMouseX() < x + width Then
		If ScaledMouseY() > y And ScaledMouseY() < y + height Then
			Return True
		EndIf
	EndIf
	Return False
End Function

Include "Source Code\Sounds.bb"

Function LoadEntities()
	;CatchErrors("LoadEntities")
	
	DrawLoading(0)
	
	Local i%
	
	For i=0 To 9
		TempSounds[i]=0
	Next
	
	PauseMenuIMG = LoadImage_Strict("GFX\menu\pausemenu.jpg")
	MaskImage PauseMenuIMG, 255,255,0
	ScaleImage PauseMenuIMG,MenuScale,MenuScale
	
	SprintIcon = LoadImage_Strict("GFX\sprinticon.png")
	BlinkIcon = LoadImage_Strict("GFX\blinkicon.png")
	CrouchIcon = LoadImage_Strict("GFX\sneakicon.png")
	WalkIcon = LoadImage_Strict("GFX\walkicon.png")
	HandIcon = LoadImage_Strict("GFX\handsymbol.png")
	HandIcon2 = LoadImage_Strict("GFX\handsymbol2.png")

	StaminaMeterIMG = LoadImage_Strict("GFX\staminameter.jpg")
	
	ButtonTexture = LoadTexture_Strict("GFX\map\KeyPad.jpg")
	ButtontextureLocked = LoadTexture_Strict("GFX\map\KeyPadLocked.jpg")

	KeypadHUD =  LoadImage_Strict("GFX\keypadhud.jpg")
	MaskImage(KeypadHUD, 255,0,255)

	Panel294 = LoadImage_Strict("GFX\294panel.jpg")
	MaskImage(Panel294, 255,0,255)
	
	
	Brightness% = GetINIFloat(OptionFile, "options", "brightness")
	CameraFogNear# = 0.5
	CameraFogFar# = 6
	StoredCameraFogFar# = CameraFogFar
	
	;TextureLodBias
	
	AmbientLightRoomTex% = CreateTexture(2,2,257)
	TextureBlend AmbientLightRoomTex,5
	SetBuffer(TextureBuffer(AmbientLightRoomTex))
	ClsColor 0,0,0
	Cls
	SetBuffer BackBuffer()
	AmbientLightRoomVal = 0
	
	SoundEmitter = CreatePivot()
	
	Camera = CreateCamera()
	CameraViewport Camera,0,0,I_Opt\GraphicWidth,I_Opt\GraphicHeight
	CameraRange(Camera, 0.05, CameraFogFar)
	CameraFogMode (Camera, 1)
	CameraFogRange (Camera, CameraFogNear, CameraFogFar)
	CameraFogColor (Camera, 0, 0, 0)
	AmbientLight Brightness, Brightness, Brightness
	
	ScreenTexs[0] = CreateTexture(512, 512, 1+256)
	ScreenTexs[1] = CreateTexture(512, 512, 1+256)
	
	CreateBlurImage()
	CameraProjMode ark_blur_cam,0
	;Listener = CreateListener(Camera)
	
	FogTexture = LoadTexture_Strict("GFX\fog.jpg", 1)
	Fog = CreateSprite(ark_blur_cam)
	ScaleSprite(Fog, 1.0, Float(I_Opt\GraphicHeight)/Float(I_Opt\GraphicWidth))
	EntityTexture(Fog, FogTexture)
	EntityBlend (Fog, 2)
	EntityOrder Fog, -1000
	MoveEntity(Fog, 0, 0, 1.0)
	
	GasMaskOverlay = LoadSprite("GFX\GasmaskOverlay.jpg", 1, ark_blur_cam)
	ScaleSprite(GasMaskOverlay, 1.0, Float(I_Opt\GraphicHeight)/Float(I_Opt\GraphicWidth))
	EntityBlend(GasMaskOverlay, 2)
	EntityFX(GasMaskOverlay, 1)
	EntityOrder GasMaskOverlay, -1003
	MoveEntity(GasMaskOverlay, 0, 0, 1.0)
	HideEntity(GasMaskOverlay)
	
	HazmatOverlay = LoadSprite("GFX\HazmatOverlay.jpg", 1, ark_blur_cam)
	ScaleSprite(HazmatOverlay, 1.0, Float(I_Opt\GraphicHeight)/Float(I_Opt\GraphicWidth))
	EntityBlend (HazmatOverlay, 2)
	EntityFX(HazmatOverlay, 1)
	EntityOrder HazmatOverlay, -1003
	MoveEntity(HazmatOverlay, 0, 0, 1.0)
	HideEntity(HazmatOverlay)
	
	InfectOverlay = LoadSprite("GFX\InfectOverlay.jpg", 1, ark_blur_cam)
	ScaleSprite(InfectOverlay, 1.0, Float(I_Opt\GraphicHeight)/Float(I_Opt\GraphicWidth))
	EntityBlend (InfectOverlay, 3)
	EntityFX(InfectOverlay, 1)
	EntityOrder InfectOverlay, -1003
	MoveEntity(InfectOverlay, 0, 0, 1.0)
	HideEntity(InfectOverlay)
	
	NVTexture = LoadTexture_Strict("GFX\NightVisionOverlay.jpg", 1)
	NVOverlay = CreateSprite(ark_blur_cam)
	ScaleSprite(NVOverlay, 1.0, Float(I_Opt\GraphicHeight)/Float(I_Opt\GraphicWidth))
	EntityTexture(NVOverlay, NVTexture)
	EntityBlend (NVOverlay, 2)
	EntityFX(NVOverlay, 1)
	EntityOrder NVOverlay, -1003
	MoveEntity(NVOverlay, 0, 0, 1.0)
	HideEntity(NVOverlay)
	
	GlassesOverlay = LoadSprite("GFX\GlassesOverlay.jpg",1,ark_blur_cam)
	ScaleSprite(GlassesOverlay, 1.0, Float(I_Opt\GraphicHeight)/Float(I_Opt\GraphicWidth))
	EntityBlend (GlassesOverlay, 2)
	EntityFX(GlassesOverlay, 1)
	EntityOrder GlassesOverlay, -1003
	MoveEntity(GlassesOverlay, 0, 0, 1.0)
	HideEntity(GlassesOverlay)
	
	FogNVTexture = LoadTexture_Strict("GFX\fogNV.jpg", 1)
	
	DrawLoading(5)
	
	DarkTexture = CreateTexture(1024, 1024, 1 + 2)
	SetBuffer TextureBuffer(DarkTexture)
	Cls
	SetBuffer BackBuffer()
	Dark = CreateSprite(ark_blur_cam)
	ScaleSprite(Dark, 1.0, Float(I_Opt\GraphicHeight)/Float(I_Opt\GraphicWidth))
	EntityTexture(Dark, DarkTexture)
	EntityBlend (Dark, 1)
	EntityOrder Dark, -1002
	MoveEntity(Dark, 0, 0, 1.0)
	EntityAlpha Dark, 0.0
	
	LightTexture = CreateTexture(1024, 1024, 1 + 2+256)
	SetBuffer TextureBuffer(LightTexture)
	ClsColor 255, 255, 255
	Cls
	ClsColor 0, 0, 0
	SetBuffer BackBuffer()
	
	TeslaTexture = LoadTexture_Strict("GFX\map\tesla.jpg", 1+2)
	
	Light = CreateSprite(ark_blur_cam)
	ScaleSprite(Light, 1.0, Float(I_Opt\GraphicHeight)/Float(I_Opt\GraphicWidth))
	EntityTexture(Light, LightTexture)
	EntityBlend (Light, 1)
	EntityOrder Light, -1002
	MoveEntity(Light, 0, 0, 1.0)
	HideEntity Light
	
	Collider = CreatePivot()
	EntityRadius Collider, 0.15, 0.30
	EntityPickMode(Collider, 1)
	EntityType Collider, HIT_PLAYER
	
	Head = CreatePivot()
	EntityRadius Head, 0.15
	EntityType Head, HIT_PLAYER
	
	
	LiquidObj = LoadMesh_Strict("GFX\items\cupliquid.b3d") ;optimized the cups dispensed by 294
	HideEntity LiquidObj
	
	VehicleObj = LoadAnimMesh_Strict("GFX\npcs\vehicle.b3d") ;Vehicle
	MTFObj = LoadAnimMesh_Strict("GFX\npcs\MTF2.b3d") ;optimized MTFs
	GuardObj = LoadAnimMesh_Strict("GFX\npcs\guard.b3d") ;optimized Guards
	;GuardTex = LoadTexture_Strict("GFX\npcs\body.jpg") ;optimized the Guards even more
	
	ClassDObj = LoadAnimMesh_Strict("GFX\npcs\classd.b3d") ;optimized Class-D's and scientists/researchers
	ApacheObj = LoadAnimMesh_Strict("GFX\apache.b3d") ;optimized Apaches (helicopters)
	ApacheRotorObj = LoadAnimMesh_Strict("GFX\apacherotor.b3d") ;optimized the Apaches even more
	
	HideEntity VehicleObj
	HideEntity MTFObj
	HideEntity GuardObj
	HideEntity ClassDObj
	HideEntity ApacheObj
	HideEntity ApacheRotorObj
	
	;Other NPCs pre-loaded

	NPC049OBJ = LoadAnimMesh_Strict("GFX\npcs\049.b3d")
	HideEntity NPC049OBJ
	NPC0492OBJ = LoadAnimMesh_Strict("GFX\npcs\zombie1.b3d")
	HideEntity NPC0492OBJ
	ClerkOBJ = LoadAnimMesh_Strict("GFX\npcs\clerk.b3d")
	HideEntity ClerkOBJ	

	
;	For i=0 To 4
;		Select True
;			Case i=2
;				tempStr="2c"
;			Case i>2
;				tempStr=Str(i)
;			Default
;				tempStr=Str(i+1)
;		End Select
;		OBJTunnel(i)=LoadRMesh("GFX\map\mt"+tempStr+".rmesh",Null)
;		HideEntity OBJTunnel(i)
;	Next
	
;	OBJTunnel(0)=LoadRMesh("GFX\map\mt1.rmesh",Null)	
;	HideEntity OBJTunnel(0)				
;	OBJTunnel(1)=LoadRMesh("GFX\map\mt2.rmesh",Null)	
;	HideEntity OBJTunnel(1)
;	OBJTunnel(2)=LoadRMesh("GFX\map\mt2c.rmesh",Null)	
;	HideEntity OBJTunnel(2)				
;	OBJTunnel(3)=LoadRMesh("GFX\map\mt3.rmesh",Null)	
;	HideEntity OBJTunnel(3)	
;	OBJTunnel(4)=LoadRMesh("GFX\map\mt4.rmesh",Null)	
;	HideEntity OBJTunnel(4)				
;	OBJTunnel(5)=LoadRMesh("GFX\map\mt_elevator.rmesh",Null)
;	HideEntity OBJTunnel(5)
;	OBJTunnel(6)=LoadRMesh("GFX\map\mt_generator.rmesh",Null)
;	HideEntity OBJTunnel(6)
	
	LightSpriteTex[0] = LoadTexture_Strict("GFX\light1.jpg", 1)
	LightSpriteTex[1] = LoadTexture_Strict("GFX\light2.jpg", 1)
	LightSpriteTex[2] = LoadTexture_Strict("GFX\lightsprite.jpg",1)
	
	DrawLoading(10)
	
	DoorOBJ = LoadMesh_Strict("GFX\map\door01.x")
	HideEntity DoorOBJ
	DoorFrameOBJ = LoadMesh_Strict("GFX\map\doorframe.x")
	HideEntity DoorFrameOBJ
	
	HeavyDoorObj(0) = LoadMesh_Strict("GFX\map\heavydoor1.x")
	HideEntity HeavyDoorObj(0)
	HeavyDoorObj(1) = LoadMesh_Strict("GFX\map\heavydoor2.x")
	HideEntity HeavyDoorObj(1)
	
	DoorColl = LoadMesh_Strict("GFX\map\doorcoll.x")
	HideEntity DoorColl
	
	ButtonOBJ = LoadMesh_Strict("GFX\map\Button.b3d")
	HideEntity ButtonOBJ
	ButtonKeyOBJ = LoadMesh_Strict("GFX\map\ButtonKeycard.b3d")
	HideEntity ButtonKeyOBJ
	ButtonCodeOBJ = LoadMesh_Strict("GFX\map\ButtonCode.b3d")
	HideEntity ButtonCodeOBJ	
	ButtonScannerOBJ = LoadMesh_Strict("GFX\map\ButtonScanner.b3d")
	HideEntity ButtonScannerOBJ	
	ButtonElevatorOBJ = LoadMesh_Strict("GFX\map\ButtonElevator.b3d")
	HideEntity(ButtonElevatorOBJ)
	
	BigDoorOBJ(0) = LoadMesh_Strict("GFX\map\ContDoorLeft.x")
	HideEntity BigDoorOBJ(0)
	BigDoorOBJ(1) = LoadMesh_Strict("GFX\map\ContDoorRight.x")
	HideEntity BigDoorOBJ(1)
	
	LeverBaseOBJ = LoadMesh_Strict("GFX\map\leverbase.x")
	HideEntity LeverBaseOBJ
	LeverOBJ = LoadMesh_Strict("GFX\map\leverhandle.x")
	HideEntity LeverOBJ
	
	DrawLoading(15)
	
	Dir = ReadDir("GFX\895pics\")
	NextFile(Dir) : NextFile(Dir)
	File$ = NextFile(Dir)
	
	i = 0
	While File <> ""
		GorePics(i) = LoadTexture_Strict("GFX\895pics\" + File)
		i = i + 1
		File = NextFile(Dir)
	Wend
	
	CloseDir(Dir)
	
	Dir = ReadDir("GFX\079pics\")
	NextFile(Dir) : NextFile(Dir)
	File$ = NextFile(Dir)
	
	i = 0
	While File <> ""
		OldAiPics[i] = LoadTexture_Strict("GFX\079pics\" + File)
		i = i + 1
		File = NextFile(Dir)
	Wend
	
	CloseDir(Dir)
	
	DrawLoading(20)
	
	For i = 0 To 6
		DecalTextures(i) = LoadTexture_Strict("GFX\decal" + (i + 1) + ".png", 1 + 2)
	Next
	DecalTextures(7) = LoadTexture_Strict("GFX\items\INVpaperstrips.jpg", 1 + 2)
	For i = 8 To 12
		DecalTextures(i) = LoadTexture_Strict("GFX\decalpd"+(i-7)+".jpg", 1 + 2)	
	Next
	For i = 13 To 14
		DecalTextures(i) = LoadTexture_Strict("GFX\bullethole"+(i-12)+".jpg", 1 + 2)	
	Next	
	For i = 15 To 16
		DecalTextures(i) = LoadTexture_Strict("GFX\blooddrop"+(i-14)+".png", 1 + 2)	
	Next
	DecalTextures(17) = LoadTexture_Strict("GFX\decal8.png", 1 + 2)	
	DecalTextures(18) = LoadTexture_Strict("GFX\decalpd6.dc", 1 + 2)	
	DecalTextures(19) = LoadTexture_Strict("GFX\decal19.png", 1 + 2)
	DecalTextures(20) = LoadTexture_Strict("GFX\decal427.png", 1 + 2)
	
	DrawLoading(25)
	
	Monitor = LoadMesh_Strict("GFX\map\monitor.b3d")
	HideEntity Monitor
	MonitorTexture = LoadTexture_Strict("GFX\monitortexture.jpg")
	
	CamBaseOBJ = LoadMesh_Strict("GFX\map\cambase.x")
	HideEntity(CamBaseOBJ)
	CamOBJ = LoadMesh_Strict("GFX\map\CamHead.b3d")
	HideEntity(CamOBJ)
	
	Monitor2 = LoadMesh_Strict("GFX\map\monitor_checkpoint.b3d")
	HideEntity Monitor2
	Monitor3 = LoadMesh_Strict("GFX\map\monitor_checkpoint.b3d")
	HideEntity Monitor3
	MonitorTexture2 = LoadTexture_Strict("GFX\map\LockdownScreen2.jpg")
	MonitorTexture3 = LoadTexture_Strict("GFX\map\LockdownScreen.jpg")
	MonitorTexture4 = LoadTexture_Strict("GFX\map\LockdownScreen3.jpg")
	MonitorTextureOff = CreateTexture(1,1)
	SetBuffer TextureBuffer(MonitorTextureOff)
	ClsColor 0,0,0
	Cls
	SetBuffer BackBuffer()
	LightConeModel = LoadMesh_Strict("GFX\lightcone.b3d")
	HideEntity LightConeModel
	
	For i = 2 To CountSurfaces(Monitor2)
		sf = GetSurface(Monitor2,i)
		b = GetSurfaceBrush(sf)
		If b<>0 Then
			t1 = GetBrushTexture(b,0)
			If t1<>0 Then
				name$ = StripPath(TextureName(t1))
				If Lower(name) <> "monitortexture.jpg"
					BrushTexture b, MonitorTextureOff, 0, 0
					PaintSurface sf,b
				EndIf
				If name<>"" Then FreeTexture t1
			EndIf
			FreeBrush b
		EndIf
	Next
	For i = 2 To CountSurfaces(Monitor3)
		sf = GetSurface(Monitor3,i)
		b = GetSurfaceBrush(sf)
		If b<>0 Then
			t1 = GetBrushTexture(b,0)
			If t1<>0 Then
				name$ = StripPath(TextureName(t1))
				If Lower(name) <> "monitortexture.jpg"
					BrushTexture b, MonitorTextureOff, 0, 0
					PaintSurface sf,b
				EndIf
				If name<>"" Then FreeTexture t1
			EndIf
			FreeBrush b
		EndIf
	Next
	
	UserTrackMusicAmount% = 0
	If EnableUserTracks Then
		Local dirPath$ = "SFX\Radio\UserTracks\"
		If FileType(dirPath)<>2 Then
			CreateDir(dirPath)
		EndIf
		
		Dir% = ReadDir("SFX\Radio\UserTracks\")
		Repeat
			file$=NextFile(Dir)
			If file$="" Then Exit
			If FileType("SFX\Radio\UserTracks\"+file$) = 1 Then
				test = LoadSound("SFX\Radio\UserTracks\"+file$)
				If test<>0
					UserTrackName$(UserTrackMusicAmount%) = file$
					UserTrackMusicAmount% = UserTrackMusicAmount% + 1
				EndIf
				FreeSound test
			EndIf
		Forever
		CloseDir Dir
	EndIf
	If EnableUserTracks Then DebugLog "User Tracks found: "+UserTrackMusicAmount
	
	InitItemTemplates()
	
	ParticleTextures[0] = LoadTexture_Strict("GFX\smoke.png", 1 + 2)
	ParticleTextures[1] = LoadTexture_Strict("GFX\flash.jpg", 1 + 2)
	ParticleTextures[2] = LoadTexture_Strict("GFX\dust.jpg", 1 + 2)
	ParticleTextures[3] = LoadTexture_Strict("GFX\npcs\hg.pt", 1 + 2)
	ParticleTextures[4] = LoadTexture_Strict("GFX\map\sun.jpg", 1 + 2)
	ParticleTextures[5] = LoadTexture_Strict("GFX\bloodsprite.png", 1 + 2)
	ParticleTextures[6] = LoadTexture_Strict("GFX\smoke2.png", 1 + 2)
	ParticleTextures[7] = LoadTexture_Strict("GFX\spark.jpg", 1 + 2)
	ParticleTextures[8] = LoadTexture_Strict("GFX\particle.png", 1 + 2)
	
	SetChunkDataValues()
	
	;NPCtypeD - different models with different textures (loaded using "CopyEntity") - ENDSHN
	For i = 0 To 10
		DTextures[i] = CopyEntity(ClassDObj)
		HideEntity DTextures[i]
	Next
	; Gonzales
	Tex = LoadTexture_Strict("GFX\npcs\gonzales.jpg")
	EntityTexture(DTextures[0], Tex)
	FreeTexture(Tex)
	; D-9341
	Tex = LoadTexture_Strict("GFX\npcs\corpse.jpg")
	EntityTexture(DTextures[1], Tex)
	FreeTexture(Tex)
	; Scientist 1
	Tex = LoadTexture_Strict("GFX\npcs\scientist.jpg")
	EntityTexture(DTextures[2], Tex)
	FreeTexture(Tex)
	; Franklin
	Tex = LoadTexture_Strict("GFX\npcs\scientist2.jpg")
	EntityTexture(DTextures[3], Tex)
	FreeTexture(Tex)
	; Janitor
	Tex = LoadTexture_Strict("GFX\npcs\janitor.jpg")
	EntityTexture(DTextures[4], Tex)
	FreeTexture(Tex)
	; Maynard
	Tex = LoadTexture_Strict("GFX\npcs\106victim.jpg")
	EntityTexture(DTextures[5], tex)
	FreeTexture(Tex)
	; Afro-American Class D
	Tex = LoadTexture_Strict("GFX\npcs\classd2.jpg")
	EntityTexture(DTextures[6], Tex)
	FreeTexture(Tex)
	; SCP-035's Victim
	Tex = LoadTexture_Strict("GFX\npcs\035victim.jpg")
	EntityTexture(DTextures[7], Tex)
	FreeTexture(Tex)
	; Body #1
	Tex = LoadTexture_Strict("GFX\npcs\body1.jpg")
	EntityTexture(DTextures[8], Tex)
	FreeTexture(Tex)
	; Body #2
	Tex = LoadTexture_Strict("GFX\npcs\body2.jpg")
	EntityTexture(DTextures[9], Tex)
	FreeTexture(Tex)
	; Body #3
	Tex = LoadTexture_Strict("GFX\npcs\body3.jpg")
	EntityTexture(DTextures[10], Tex)
	FreeTexture(Tex)
	
	LoadMaterials("DATA\materials.ini")
	
	OBJTunnel(0)=LoadRMesh("GFX\map\mt1.rmesh",Null)	
	HideEntity OBJTunnel(0)				
	OBJTunnel(1)=LoadRMesh("GFX\map\mt2.rmesh",Null)	
	HideEntity OBJTunnel(1)
	OBJTunnel(2)=LoadRMesh("GFX\map\mt2c.rmesh",Null)	
	HideEntity OBJTunnel(2)				
	OBJTunnel(3)=LoadRMesh("GFX\map\mt3.rmesh",Null)	
	HideEntity OBJTunnel(3)	
	OBJTunnel(4)=LoadRMesh("GFX\map\mt4.rmesh",Null)	
	HideEntity OBJTunnel(4)				
	OBJTunnel(5)=LoadRMesh("GFX\map\mt_elevator.rmesh",Null)
	HideEntity OBJTunnel(5)
	OBJTunnel(6)=LoadRMesh("GFX\map\mt_generator.rmesh",Null)
	HideEntity OBJTunnel(6)
	
	;TextureLodBias TextureBias
	TextureLodBias TextureFloat#
	;Devil Particle System
	;ParticleEffect[] numbers:
	;	0 - electric spark
	;	1 - smoke effect
	
	Local t0
	
	InitParticles(Camera)
	
	;Spark Effect (short)
	ParticleEffect[0] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[0], 3)
	SetTemplateInterval(ParticleEffect[0], 1)
	SetTemplateParticlesPerInterval(ParticleEffect[0], 6)
	SetTemplateEmitterLifeTime(ParticleEffect[0], 6)
	SetTemplateParticleLifeTime(ParticleEffect[0], 20, 30)
	SetTemplateTexture(ParticleEffect[0], "GFX\Spark.png", 2, 3)
	SetTemplateOffset(ParticleEffect[0], -0.1, 0.1, -0.1, 0.1, -0.1, 0.1)
	SetTemplateVelocity(ParticleEffect[0], -0.0375, 0.0375, -0.0375, 0.0375, -0.0375, 0.0375)
	SetTemplateAlignToFall(ParticleEffect[0], True, 45)
	SetTemplateGravity(ParticleEffect[0], 0.001)
	SetTemplateAlphaVel(ParticleEffect[0], True)
	;SetTemplateSize(ParticleEffect[0], 0.0625, 0.125, 0.7, 1)
	SetTemplateSize(ParticleEffect[0], 0.03125, 0.0625, 0.7, 1)
	SetTemplateColors(ParticleEffect[0], $0000FF, $6565FF)
	SetTemplateFloor(ParticleEffect[0], 0.0, 0.5)
	
	;Smoke effect (for some vents)
	ParticleEffect[1] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[1], 1)
	SetTemplateInterval(ParticleEffect[1], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[1], 3)
	SetTemplateParticleLifeTime(ParticleEffect[1], 30, 45)
	SetTemplateTexture(ParticleEffect[1], "GFX\smoke2.png", 2, 1)
	;SetTemplateOffset(ParticleEffect[1], -.3, .3, -.3, .3, -.3, .3)
	SetTemplateOffset(ParticleEffect[1], 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
	;SetTemplateVelocity(ParticleEffect[1], -.04, .04, .1, .2, -.04, .04)
	SetTemplateVelocity(ParticleEffect[1], 0.0, 0.0, 0.02, 0.025, 0.0, 0.0)
	SetTemplateAlphaVel(ParticleEffect[1], True)
	;SetTemplateSize(ParticleEffect[1], 3, 3, .5, 1.5)
	SetTemplateSize(ParticleEffect[1], 0.4, 0.4, 0.5, 1.5)
	SetTemplateSizeVel(ParticleEffect[1], .01, 1.01)
	
	;Smoke effect (for decontamination gas)
	ParticleEffect[2] = CreateTemplate()
	SetTemplateEmitterBlend(ParticleEffect[2], 1)
	SetTemplateInterval(ParticleEffect[2], 1)
	SetTemplateEmitterLifeTime(ParticleEffect[2], 3)
	SetTemplateParticleLifeTime(ParticleEffect[2], 30, 45)
	SetTemplateTexture(ParticleEffect[2], "GFX\smoke.png", 2, 1)
	SetTemplateOffset(ParticleEffect[2], -0.1, 0.1, -0.1, 0.1, -0.1, 0.1)
	SetTemplateVelocity(ParticleEffect[2], -0.005, 0.005, 0.0, -0.03, -0.005, 0.005)
	SetTemplateAlphaVel(ParticleEffect[2], True)
	SetTemplateSize(ParticleEffect[2], 0.4, 0.4, 0.5, 1.5)
	SetTemplateSizeVel(ParticleEffect[2], .01, 1.01)
	SetTemplateGravity(ParticleEffect[2], 0.005)
	t0 = CreateTemplate()
	SetTemplateEmitterBlend(t0, 1)
	SetTemplateInterval(t0, 1)
	SetTemplateEmitterLifeTime(t0, 3)
	SetTemplateParticleLifeTime(t0, 30, 45)
	SetTemplateTexture(t0, "GFX\smoke2.png", 2, 1)
	SetTemplateOffset(t0, -0.1, 0.1, -0.1, 0.1, -0.1, 0.1)
	SetTemplateVelocity(t0, -0.005, 0.005, 0.0, -0.03, -0.005, 0.005)
	SetTemplateAlphaVel(t0, True)
	SetTemplateSize(t0, 0.4, 0.4, 0.5, 1.5)
	SetTemplateSizeVel(t0, .01, 1.01)
	SetTemplateGravity(ParticleEffect[2], 0.005)
	SetTemplateSubTemplate(ParticleEffect[2], t0)
	
	DrawLoading(30)
	
	;LoadRoomMeshes()
	
	;CatchErrors("Uncaught LoadEntities")
End Function

Function InitNewGame(zone%=0)
	;CatchErrors("InitNewGame")
	Local i%, de.Decals, d.Doors, it.Items, r.Rooms, sc.SecurityCams, e.Events
	
	LoadEntities()
	LoadAllSounds()
	
	DrawLoading(45)
	
	HeartBeatRate = 70
	
	AccessCode = 0
	For i = 0 To 3
		AccessCode = AccessCode + Rand(1,9)*(10^i)
	Next	
	
	CreateMap(zone)
	InitWayPoints()
	
	DrawLoading(79)
	
	Curr173 = CreateNPC(NPCtype173, 0, -30.0, 0)
	Curr106 = CreateNPC(NPCtypeOldMan, 0, -30.0, 0)
	Curr106\State = 70 * 60 * Rand(12,17)
	
	For d.Doors = Each Doors
		EntityParent(d\obj, 0)
		If d\obj2 <> 0 Then EntityParent(d\obj2, 0)
		If d\frameobj <> 0 Then EntityParent(d\frameobj, 0)
		If d\buttons[0] <> 0 Then EntityParent(d\buttons[0], 0)
		If d\buttons[1] <> 0 Then EntityParent(d\buttons[1], 0)
		
		If d\obj2 <> 0 And d\dir = 0 Then
			MoveEntity(d\obj, 0, 0, 8.0 * RoomScale)
			MoveEntity(d\obj2, 0, 0, 8.0 * RoomScale)
		EndIf	
	Next
	
	For it.Items = Each Items
		EntityType (it\collider, HIT_ITEM)
		EntityParent(it\collider, 0)
	Next
	
	DrawLoading(80)
	For sc.SecurityCams = Each SecurityCams
		sc\angle = EntityYaw(sc\obj) + sc\angle
		EntityParent(sc\obj, 0)
	Next	
	
	For r.Rooms = Each Rooms
		For i = 0 To MaxRoomLights-1
			If r\Lights[i]<>0 Then EntityParent(r\Lights[i],0)
		Next
		
		If (Not r\RoomTemplate\DisableDecals) Then
			If Rand(4) = 1 Then
				de.Decals = CreateDecal(Rand(2, 3), EntityX(r\obj)+Rnd(- 2,2), 0.003, EntityZ(r\obj)+Rnd(-2,2), 90, Rand(360), 0)
				de\Size = Rnd(0.1, 0.4) : ScaleSprite(de\obj, de\Size, de\Size)
				EntityAlpha(de\obj, Rnd(0.85, 0.95))
			EndIf
			
			If Rand(4) = 1 Then
				de.Decals = CreateDecal(0, EntityX(r\obj)+Rnd(- 2,2), 0.003, EntityZ(r\obj)+Rnd(-2,2), 90, Rand(360), 0)
				de\Size = Rnd(0.5, 0.7) : EntityAlpha(de\obj, 0.7) : de\ID = 1 : ScaleSprite(de\obj, de\Size, de\Size)
				EntityAlpha(de\obj, Rnd(0.7, 0.85))
			EndIf
		EndIf
		
		If (r\RoomTemplate\Name = "start" And IntroEnabled = False) Then 
			PositionEntity (Collider, EntityX(r\obj)+3584*RoomScale, 704*RoomScale, EntityZ(r\obj)+1024*RoomScale)
			PlayerRoom = r
		ElseIf (r\RoomTemplate\Name = "room173" And IntroEnabled) Then
			PositionEntity (Collider, EntityX(r\obj), 1.0, EntityZ(r\obj))
			PlayerRoom = r
		EndIf
		
	Next
	
	Local rt.RoomTemplates
	For rt.RoomTemplates = Each RoomTemplates
		FreeEntity (rt\obj)
		rt\obj = 0
	Next	
	
	Local tw.TempWayPoints
	For tw.TempWayPoints = Each TempWayPoints
		Delete tw
	Next
	
	TurnEntity(Collider, 0, Rand(160, 200), 0)
	
	ResetEntity Collider
	
	InitEvents()
	
	For e.Events = Each Events
		If e\EventName = "room2nuke"
			e\EventState = 1
			DebugLog "room2nuke"
		EndIf
		If e\EventName = "room106"
			e\EventState2 = 1
			DebugLog "room106"
		EndIf	
		If e\EventName = "room2sl"
			e\EventState3 = 1
			DebugLog "room2sl"
		EndIf
	Next
	
	MoveMouse viewport_center_x,viewport_center_y;320, 240
	
	SetFont I_Opt\Fonts[1]
	
	HidePointer()
	
	BlinkTimer = -10
	BlurTimer = 100
	Stamina = 100
	
	For i% = 0 To 70
		FPSfactor = 1.0
		FlushKeys()
		MovePlayer()
		UpdateDoors()
		UpdateNPCs()
		UpdateWorld()
		;Cls
		If (Int(Float(i)*0.27)<>Int(Float(i-1)*0.27)) Then
			DrawLoading(80+Int(Float(i)*0.27))
		EndIf
	Next
	
	FreeTextureCache
	DrawLoading(100)
	
	FlushKeys
	FlushMouse
	
	DropSpeed = 0
	
	PrevTime = MilliSecs()
	;CatchErrors("Uncaught InitNewGame")
End Function

Function InitLoadGame()
	;CatchErrors("InitLoadGame")
	Local d.Doors, sc.SecurityCams, rt.RoomTemplates, e.Events
	
	DrawLoading(80)
	
	For d.Doors = Each Doors
		EntityParent(d\obj, 0)
		If d\obj2 <> 0 Then EntityParent(d\obj2, 0)
		If d\frameobj <> 0 Then EntityParent(d\frameobj, 0)
		If d\buttons[0] <> 0 Then EntityParent(d\buttons[0], 0)
		If d\buttons[1] <> 0 Then EntityParent(d\buttons[1], 0)
		
	Next
	
	For sc.SecurityCams = Each SecurityCams
		sc\angle = EntityYaw(sc\obj) + sc\angle
		EntityParent(sc\obj, 0)
	Next
	
	ResetEntity Collider
	
	;InitEvents()
	
	DrawLoading(90)
	
	MoveMouse viewport_center_x,viewport_center_y
	
	SetFont I_Opt\Fonts[1]
	
	HidePointer()
	
	BlinkTimer = BLINKFREQ
	Stamina = 100
	
	For rt.RoomTemplates = Each RoomTemplates
		If rt\obj <> 0 Then FreeEntity(rt\obj) : rt\obj = 0
	Next
	
	DropSpeed = 0.0
	
	For e.Events = Each Events
		;Loading the necessary stuff for dimension1499, but this will only be done if the player is in this dimension already
		If e\EventName = "dimension1499"
			If e\EventState = 2

				DrawLoading(91)
				e\room\Objects[0] = CreatePlane()
				Local planetex% = LoadTexture_Strict("GFX\map\dimension1499\grit3.jpg")
				EntityTexture e\room\Objects[0],planetex%
				FreeTexture planetex%
				PositionEntity e\room\Objects[0],0,EntityY(e\room\obj),0
				EntityType e\room\Objects[0],HIT_MAP
				;EntityParent e\room\Objects[0],e\room\obj
				DrawLoading(92)
				NTF_1499Sky = sky_CreateSky("GFX\map\sky\1499sky")
				DrawLoading(93)
				For i = 1 To 15
					e\room\Objects[i] = LoadMesh_Strict("GFX\map\dimension1499\1499object"+i+".b3d")
					HideEntity e\room\Objects[i]
				Next
				DrawLoading(96)
				CreateChunkParts(e\room)
				DrawLoading(97)
				x# = EntityX(e\room\obj)
				z# = EntityZ(e\room\obj)
				Local ch.Chunk
				For i = -2 To 2 Step 2
					ch = CreateChunk(-1,x#*(i*2.5),EntityY(e\room\obj),z#)
				Next
				DrawLoading(98)
				UpdateChunks(e\room,15,False)
				;MoveEntity Collider,0,10,0
				;ResetEntity Collider
				
				DebugLog "Loaded dimension1499 successful"
				
				Exit

			EndIf
		EndIf
	Next
	
	FreeTextureCache
	
	;CatchErrors("Uncaught InitLoadGame")
	DrawLoading(100)
	
	PrevTime = MilliSecs()
	FPSfactor = 0
	ResetInput()
	
End Function

Function NullGame(playbuttonsfx%=True)
	;CatchErrors("NullGame")
	Local i%, x%, y%, lvl
	Local itt.ItemTemplates, s.Screens, lt.LightTemplates, d.Doors, m.Materials
	Local wp.WayPoints, twp.TempWayPoints, r.Rooms, it.Items
	
	KillSounds()
	If playbuttonsfx Then PlaySound_Strict ButtonSFX
	
	FreeParticles()
	
	ClearTextureCache
	
	DebugHUD = False
	
	UnableToMove% = False
	
	QuickLoad_CurrEvent = Null
	
	DeathMSG$=""
	
	UsedConsole = False
	
	DoorTempID = 0
	RoomTempID = 0
	
	PlayTime = 0
	PlayingSince = 0
	
	GameSaved = 0
	
	HideDistance# = 15.0
	
	For x = 0 To MapWidth+1
		For y = 0 To MapHeight+1
			MapTemp(x, y) = 0
			MapFound(x, y) = 0
		Next
	Next
	
	For itt.ItemTemplates = Each ItemTemplates
		itt\found = False
	Next
	
	InvertCam = 0
	
	DropSpeed = 0
	Shake = 0
	CurrSpeed = 0
	
	DeathTimer=0
	
	HeartBeatVolume = 0
	
	StaminaEffect = 1.0
	StaminaEffectTimer = 0
	BlinkEffect = 1.0
	BlinkEffectTimer = 0
	
	Bloodloss = 0
	Injuries = 0
	Infect = 0
	
	For i = 0 To 6
		SCP1025state[i] = 0
	Next
	
	SelectedEnding = ""
	EndingTimer = 0
	ExplosionTimer = 0
	
	CameraShake = 0
	Shake = 0
	LightFlash = 0
	
	ClearCheats(I_Cheats)
	WireFrame 0
	WearingGasMask = 0
	WearingHazmat = 0
	WearingVest = 0
	Wearing178 = 0
	Wearing714 = 0
	WearingScramble = 0
	If WearingNightVision<>0 Then
		CameraFogFar = StoredCameraFogFar
		WearingNightVision = 0
	EndIf
	
	Delete I_427 ;Just delete it and create it anew as all values have a default of 0
	I_427 = New SCP427
	
	ForceMove = 0.0
	ForceAngle = 0.0	
	Playable = True
	
	CoffinDistance = 100
	
	Contained106 = False
	If Curr173 <> Null Then Curr173\Idle = False
	
	MTFtimer = 0
	For i = 0 To 6
		MTFrooms[i]=Null
		MTFroomState[i]=0
	Next
	
	For s.Screens = Each Screens
		If s\img <> 0 Then FreeImage s\img : s\img = 0
		Delete s
	Next
	
	For i = 0 To MAXACHIEVEMENTS-1
		Achievements[i]=0
	Next
	RefinedItems = 0
	
	ConsoleInput = ""
	ConsoleOpen = False
	
	EyeIrritation = 0
	EyeStuck = 0
	
	ShouldPlay = 0
	
	KillTimer = 0
	FallTimer = 0
	Stamina = 100
	BlurTimer = 0
	Sanity = 0
	RestoreSanity = True
	Crouch = False
	CrouchState = 0.0
	LightVolume = 0.0
	Vomit = False
	VomitTimer = 0.0
	SecondaryLightOn# = True
	PrevSecondaryLightOn# = True
	RemoteDoorOn = True
	SoundTransmission = False
	
	Msg = ""
	MsgTimer = 0
	
	SelectedItem = Null
	
	For i = 0 To MaxItemAmount - 1
		Inventory(i) = Null
	Next
	SelectedItem = Null
	
	ClosestButton = 0
	
	For d.Doors = Each Doors
		Delete d
	Next
	
	;ClearWorld
	
	For lt.LightTemplates = Each LightTemplates
		Delete lt
	Next 
	
	For m.Materials = Each Materials
		Delete m
	Next
	
	For wp.WayPoints = Each WayPoints
		Delete wp
	Next
	
	For twp.TempWayPoints = Each TempWayPoints
		Delete twp
	Next
	
	Delete Each TempFluLight
	Delete Each FluLight
	
	For r.Rooms = Each Rooms
		Delete r
	Next
	
	For itt.ItemTemplates = Each ItemTemplates
		Delete itt
	Next 
	
	For it.Items = Each Items
		Delete it
	Next
	
	For pr.Props = Each Props
		Delete pr
	Next
	
	For de.decals = Each Decals
		Delete de
	Next
	
	For n.NPCS = Each NPCs
		Delete n
	Next
	Curr173 = Null
	Curr106 = Null
	Curr096 = Null
	For i = 0 To 6
		MTFrooms[i]=Null
	Next
	ForestNPC = 0
	ForestNPCTex = 0
	
	Local e.Events
	For e.Events = Each Events
		If e\Sound<>0 Then FreeSound_Strict e\Sound
		If e\Sound2<>0 Then FreeSound_Strict e\Sound2
		Delete e
	Next
	
	For sc.securitycams = Each SecurityCams
		Delete sc
	Next
	
	For em.emitters = Each Emitters
		Delete em
	Next	
	
	For p.particles = Each Particles
		Delete p
	Next
	
	For rt.RoomTemplates = Each RoomTemplates
		rt\obj = 0
	Next
	
	For i = 0 To 5
		If ChannelPlaying(RadioCHN[i]) Then StopChannel(RadioCHN[i])
	Next
	
	NTF_1499PrevX# = 0.0
	NTF_1499PrevY# = 0.0
	NTF_1499PrevZ# = 0.0
	NTF_1499PrevRoom = Null
	NTF_1499X# = 0.0
	NTF_1499Y# = 0.0
	NTF_1499Z# = 0.0
	Wearing1499% = False
	DeleteChunks()
	
	DeleteDevilEmitters()
	
	OptionsMenu% = -1
	QuitMSG% = -1
	AchievementsMenu% = -1
	
	MusicVolume# = PrevMusicVolume
	SFXVolume# = PrevSFXVolume
	DeafPlayer% = False
	DeafTimer# = 0.0
	
	IsZombie% = False
	
	Delete Each AchievementMsg
	CurrAchvMSGID = 0
	
	;DeInitExt
	
	ClearWorld
	Camera = 0
	ark_blur_cam = 0
	Collider = 0
	Sky = 0
	InitFastResize()
	
	;CatchErrors("Uncaught NullGame")
End Function

Function Use294()
	
	Local x#,y#, xtemp%,ytemp%, strtemp$, temp%
	
	ShowPointer()
	
	x = I_Opt\GraphicWidth/2 - (ImageWidth(Panel294)/2)
	y = I_Opt\GraphicHeight/2 - (ImageHeight(Panel294)/2)
	DrawImage Panel294, x, y
	
	If I_Opt\GraphicMode = 0 Then DrawImage CursorIMG, ScaledMouseX(),ScaledMouseY()
	
	temp = True
	If PlayerRoom\SoundCHN<>0 Then temp = False
	
	Text x+907, y+185, Input294, True,True
	
	If temp Then
		If MouseHit1 Then
			xtemp = Floor((ScaledMouseX()-x-228) / 35.5)
			ytemp = Floor((ScaledMouseY()-y-342) / 36.5)
			
			If ytemp => 0 And ytemp < 5 Then
				If xtemp => 0 And xtemp < 10 Then PlaySound_Strict ButtonSFX
			EndIf
			
			strtemp = ""
			
			temp = False
			
			Select ytemp
				Case 0
					strtemp = (xtemp + 1) Mod 10
				Case 1
					Select xtemp
						Case 0
							strtemp = "Q"
						Case 1
							strtemp = "W"
						Case 2
							strtemp = "E"
						Case 3
							strtemp = "R"
						Case 4
							strtemp = "T"
						Case 5
							strtemp = "Y"
						Case 6
							strtemp = "U"
						Case 7
							strtemp = "I"
						Case 8
							strtemp = "O"
						Case 9
							strtemp = "P"
					End Select
				Case 2
					Select xtemp
						Case 0
							strtemp = "A"
						Case 1
							strtemp = "S"
						Case 2
							strtemp = "D"
						Case 3
							strtemp = "F"
						Case 4
							strtemp = "G"
						Case 5
							strtemp = "H"
						Case 6
							strtemp = "J"
						Case 7
							strtemp = "K"
						Case 8
							strtemp = "L"
						Case 9 ;dispense
							temp = True
					End Select
				Case 3
					Select xtemp
						Case 0
							strtemp = "Z"
						Case 1
							strtemp = "X"
						Case 2
							strtemp = "C"
						Case 3
							strtemp = "V"
						Case 4
							strtemp = "B"
						Case 5
							strtemp = "N"
						Case 6
							strtemp = "M"
						Case 7
							strtemp = "-"
						Case 8
							strtemp = " "
						Case 9
							Input294 = Left(Input294, Max(Len(Input294)-1,0))
					End Select
				Case 4
					strtemp = " "
			End Select
			
			Input294 = Input294 + strtemp
			
			Input294 = Left(Input294, Min(Len(Input294),15))
			
			If temp And Input294<>"" Then ;dispense
				Input294 = Trim(Lower(Input294))
				If Left(Input294, Min(7,Len(Input294))) = "cup of " Then
					Input294 = Right(Input294, Len(Input294)-7)
				ElseIf Left(Input294, Min(9,Len(Input294))) = "a cup of " 
					Input294 = Right(Input294, Len(Input294)-9)
				EndIf
				
				If Input294<>""
					Local loc% = GetINISectionLocation(Data294,Input294)
				EndIf
				
				If loc > 0 Then
					strtemp$ = GetINIString2(Data294, loc, "dispensesound")
					If strtemp="" Then
						PlayerRoom\SoundCHN = PlaySound_Strict (LoadTempSound("SFX\SCP\294\dispense1.ogg"))
					Else
						PlayerRoom\SoundCHN = PlaySound_Strict (LoadTempSound(strtemp))
					EndIf
					
					If GetINIInt2(Data294, loc, "explosion")=True Then 
						ExplosionTimer = 135
						DeathMSG = GetINIString2(Data294, loc, "deathmessage")
					EndIf
					
					strtemp$ = GetINIString2(Data294, loc, "color")
					
					sep1 = Instr(strtemp, ",", 1)
					sep2 = Instr(strtemp, ",", sep1+1)
					r% = Trim(Left(strtemp, sep1-1))
					g% = Trim(Mid(strtemp, sep1+1, sep2-sep1-1))
					b% = Trim(Right(strtemp, Len(strtemp)-sep2))
					
					alpha# = Float(GetINIString2(Data294, loc, "alpha",1.0))
					glow = GetINIInt2(Data294, loc, "glow")
					;If alpha = 0 Then alpha = 1.0
					If glow Then alpha = -alpha
					
					it.items = CreateItem("cup", EntityX(PlayerRoom\Objects[1],True),EntityY(PlayerRoom\Objects[1],True),EntityZ(PlayerRoom\Objects[1],True), "", r,g,b,alpha)
					it\localname = GetLocalString("Items", "fullcup") + " " +Input294
					EntityType (it\collider, HIT_ITEM)
					
				Else
					;out of range
					Input294 = GetLocalString("Game", "oor")
					PlayerRoom\SoundCHN = PlaySound_Strict (LoadTempSound("SFX\SCP\294\outofrange.ogg"))
				EndIf
				
			EndIf
			
		EndIf ;if mousehit1
		
		If MouseHit2 Lor (Not Using294) Then 
			HidePointer()
			Using294 = False
			Input294 = ""
			MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
		EndIf
		
	Else ;playing a dispensing sound
		If Input294 <> GetLocalString("Game", "oor") Then Input294 = GetLocalString("Game", "disp")
		
		If Not ChannelPlaying(PlayerRoom\SoundCHN) Then
			If Input294 <> GetLocalString("Game", "oor") Then
				HidePointer()
				Using294 = False
				MouseXSpeed() : MouseYSpeed() : MouseZSpeed() : mouse_x_speed_1#=0.0 : mouse_y_speed_1#=0.0
				Local e.Events
				For e.Events = Each Events
					If e\room = PlayerRoom
						e\EventState2 = 0
						Exit
					EndIf
				Next
			EndIf
			Input294=""
			PlayerRoom\SoundCHN=0
		EndIf
	EndIf
	
End Function

Function Use427()
	Local i%,pvt%,de.Decals,tempchn%
	Local prevI427Timer# = I_427\Timer
	
	If I_427\Timer < 70*360
		If I_427\Using>0 Then
			I_427\Timer = I_427\Timer + FPSfactor * I_427\Using
			If Injuries > 0.0 Then
				Injuries = Max(Injuries - 0.0005 * FPSfactor,0.0)
			EndIf
			If Bloodloss > 0.0 And Injuries <= 1.0 Then
				Bloodloss = Max(Bloodloss - 0.001 * FPSfactor,0.0)
			EndIf
			If Infect > 0.0 Then
				Infect = Max(Infect - 0.001 * FPSfactor,0.0)
			EndIf
			For i = 0 To 6
				If SCP1025state[i]>0.0 Then
					SCP1025state[i] = Max(SCP1025state[i] - 0.001 * FPSfactor * SCP1025state[7],0.0)
				EndIf
			Next
			If I_427\Sound[0]=0 Then
				I_427\Sound[0] = LoadSound_Strict("SFX\SCP\427\Effect.ogg")
			EndIf
			If (Not ChannelPlaying(I_427\SoundCHN[0])) Then
				I_427\SoundCHN[0] = PlaySound_Strict(I_427\Sound[0])
			EndIf
			If I_427\Timer => 70*180 Then
				If I_427\Sound[1]=0 Then
					I_427\Sound[1] = LoadSound_Strict("SFX\SCP\427\Transform.ogg")
				EndIf
				If (Not ChannelPlaying(I_427\SoundCHN[1])) Then
					I_427\SoundCHN[1] = PlaySound_Strict(I_427\Sound[1])
				EndIf
			EndIf
			If prevI427Timer < 70*60 And I_427\Timer => 70*60 Then
				Msg = GetLocalString("Messages", "4271")
				MsgTimer = 70*5
			ElseIf prevI427Timer < 70*180 And I_427\Timer => 70*180 Then
				Msg = GetLocalString("Messages", "4272")
				MsgTimer = 70*5
			EndIf
		Else
			For i = 0 To 1
				If I_427\SoundCHN[i]<>0 Then
					If ChannelPlaying(I_427\SoundCHN[i]) Then
						StopChannel(I_427\SoundCHN[i])
					EndIf
				EndIf
			Next
		EndIf
	Else
		If prevI427Timer-FPSfactor < 70*360 And I_427\Timer => 70*360 Then
			Msg = GetLocalString("Messages", "4273")
			MsgTimer = 70*5
		ElseIf prevI427Timer-FPSfactor < 70*390 And I_427\Timer => 70*390 Then
			Msg = GetLocalString("Messages", "4274")
			MsgTimer = 70*5
		EndIf
		I_427\Timer = I_427\Timer + FPSfactor
		If I_427\Sound[0]=0 Then
			I_427\Sound[0] = LoadSound_Strict("SFX\SCP\427\Effect.ogg")
		EndIf
		If I_427\Sound[1]=0 Then
			I_427\Sound[1] = LoadSound_Strict("SFX\SCP\427\Transform.ogg")
		EndIf
		For i = 0 To 1
			If (Not ChannelPlaying(I_427\SoundCHN[i])) Then
				I_427\SoundCHN[i] = PlaySound_Strict(I_427\Sound[i])
			EndIf
		Next
		If Rnd(200)<2.0 Then
			pvt = CreatePivot()
			PositionEntity pvt, EntityX(Collider)+Rnd(-0.05,0.05),EntityY(Collider)-0.05,EntityZ(Collider)+Rnd(-0.05,0.05)
			TurnEntity pvt, 90, 0, 0
			EntityPick(pvt,0.3)
			de.Decals = CreateDecal(20, PickedX(), PickedY()+0.005, PickedZ(), 90, Rand(360), 0)
			de\Size = Rnd(0.03,0.08)*2.0 : EntityAlpha(de\obj, 1.0) : ScaleSprite de\obj, de\Size, de\Size
			tempchn% = PlaySound_Strict (DripSFX(Rand(0,2)))
			ChannelVolume tempchn, Rnd(0.0,0.8)*SFXVolume
			ChannelPitch tempchn, Rand(20000,30000)
			FreeEntity pvt
			BlurTimer = 800
		EndIf
		If I_427\Timer >= 70*420 Then
			Kill()
			DeathMSG = GetLocalString("Deaths", "427")
		ElseIf I_427\Timer >= 70*390 Then
			SetCrouch(True)
		EndIf
	EndIf
	
End Function


Function UpdateMTF%()
	If PlayerRoom\RoomTemplate\Name = "gateaentrance" Then Return
	
	Local r.Rooms, n.NPCs
	Local dist#, i%
	
	;mtf ei vielä spawnannut, spawnataan jos pelaaja menee tarpeeksi lähelle gate b:tä
	If MTFtimer = 0 Then
		If Rand(30)=1 And PlayerRoom\RoomTemplate\Name$ <> "dimension1499" Then
			
			Local entrance.Rooms = Null
			For r.Rooms = Each Rooms
				If Lower(r\RoomTemplate\Name) = "gateaentrance" Then entrance = r : Exit
			Next
			
			If entrance <> Null Then
				If CurrentZone = 2 Then
					If Abs(EntityZ(entrance\obj)-EntityZ(Collider))<30.0 Then
						If PlayerInReachableRoom()
							PlayAnnouncement("SFX\Character\MTF\Announc.ogg")
						EndIf
						
						MTFtimer = FPSfactor
						Local leader.NPCs
						For i = 0 To 2
							n.NPCs = CreateNPC(NPCtypeMTF, EntityX(entrance\obj)+0.3*(i-1), EntityY(entrance\obj)+1.0,EntityZ(entrance\obj)+8.0)
							
							If i = 0 Then 
								leader = n
							Else
								n\MTFLeader = leader
							EndIf
							
							n\PrevX = i
						Next
					EndIf
				EndIf
			EndIf
		EndIf
	Else
		If MTFtimer <= 70*120 ;70*120
			MTFtimer = MTFtimer + FPSfactor
		ElseIf MTFtimer > 70*120 And MTFtimer < 10000
			If PlayerInReachableRoom()
				PlayAnnouncement("SFX\Character\MTF\AnnouncAfter1.ogg")
			EndIf
			MTFtimer = 10000
		ElseIf MTFtimer >= 10000 And MTFtimer <= 10000+(70*120) ;70*120
			MTFtimer = MTFtimer + FPSfactor
		ElseIf MTFtimer > 10000+(70*120) And MTFtimer < 20000
			If PlayerInReachableRoom()
				PlayAnnouncement("SFX\Character\MTF\AnnouncAfter2.ogg")
			EndIf
			MTFtimer = 20000
		ElseIf MTFtimer >= 20000 And MTFtimer <= 20000+(70*60) ;70*120
			MTFtimer = MTFtimer + FPSfactor
		ElseIf MTFtimer > 20000+(70*60) And MTFtimer < 25000
			If PlayerInReachableRoom()
				;If the player has an SCP in their inventory play special voice line.
				For i = 0 To MaxItemAmount-1
					If Inventory(i) <> Null Then
						If (Left(Inventory(i)\itemtemplate\tempname, 3) = "scp") And (Left(Inventory(i)\itemtemplate\namespec, 3) <> "035") And (Left(Inventory(i)\itemtemplate\namespec, 3) <> "093")
							PlayAnnouncement("SFX\Character\MTF\ThreatAnnouncPossession.ogg")
							MTFtimer = 25000
							Return
							Exit
						EndIf
					EndIf
				Next
				
				PlayAnnouncement("SFX\Character\MTF\ThreatAnnounc"+Rand(1,3)+".ogg")
			EndIf
			MTFtimer = 25000
			
		ElseIf MTFtimer >= 25000 And MTFtimer <= 25000+(70*60) ;70*120
			MTFtimer = MTFtimer + FPSfactor
		ElseIf MTFtimer > 25000+(70*60) And MTFtimer < 30000
			If PlayerInReachableRoom()
				PlayAnnouncement("SFX\Character\MTF\ThreatAnnouncFinal.ogg")
			EndIf
			MTFtimer = 30000
			
		EndIf
	EndIf
	
End Function


Function UpdateInfect()
	Local temp#, i%, r.Rooms
	
	Local teleportForInfect% = True
	
	If PlayerRoom\RoomTemplate\Name = "room860"
		For e.Events = Each Events
			If e\EventName = "room860"
				If e\EventState = 1.0
					teleportForInfect = False
				EndIf
				Exit
			EndIf
		Next
	ElseIf PlayerRoom\RoomTemplate\Name = "dimension1499" Lor PlayerRoom\RoomTemplate\Name = "pocketdimension" Lor PlayerRoom\RoomTemplate\Name = "gatea"
		teleportForInfect = False
	ElseIf PlayerRoom\RoomTemplate\Name = "gateb" And EntityY(Collider)>1040.0*RoomScale
		teleportForInfect = False
	EndIf
	
	If Infect>0 Then
		ShowEntity InfectOverlay
		
		If Infect < 93.0 Then
			temp=Infect
			If (I_427\Using = 0 And I_427\Timer < 70*360) Then
				Infect = Min(Infect+FPSfactor*0.002,100)
			EndIf
			
			BlurTimer = Max(Infect*3*(2.0-CrouchState),BlurTimer)
			
			HeartBeatRate = Max(HeartBeatRate, 100)
			HeartBeatVolume = Max(HeartBeatVolume, Infect/120.0)
			
			EntityAlpha InfectOverlay, Min((PowTwo(Infect*0.2))/1000.0,0.5) * (Sin(MilliSecs()/8.0)+2.0)
			
			For i = 0 To 6
				If Infect>i*15+10 And temp =< i*15+10 Then
					PlaySound_Strict LoadTempSound("SFX\SCP\008\Voices"+i+".ogg")
				EndIf
			Next
			
			If Infect > 20 And temp =< 20.0 Then
				Msg = GetLocalString("Messages", "infect1")
				MsgTimer = 70*6
			ElseIf Infect > 40 And temp =< 40.0
				Msg = GetLocalString("Messages", "infect2")
				MsgTimer = 70*6
			ElseIf Infect > 60 And temp =< 60.0
				Msg = GetLocalString("Messages", "infect3")
				MsgTimer = 70*6
			ElseIf Infect > 80 And temp =< 80.0
				Msg = GetLocalString("Messages", "infect4")
				MsgTimer = 70*6
			ElseIf Infect =>91.5
				BlinkTimer = Max(Min(-10*(Infect-91.5),BlinkTimer),-10)
				IsZombie = True
				UnableToMove = True
				If Infect >= 92.7 And temp < 92.7 Then
					If teleportForInfect
						For r.Rooms = Each Rooms
							If r\RoomTemplate\Name="room008" Then
								PositionEntity Collider, EntityX(r\Objects[7],True),EntityY(r\Objects[7],True),EntityZ(r\Objects[7],True),True
								ResetEntity Collider
								r\NPC[0] = CreateNPC(NPCtypeD, EntityX(r\Objects[6],True),EntityY(r\Objects[6],True)+0.2,EntityZ(r\Objects[6],True))
								r\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\008\KillScientist1.ogg")
								r\NPC[0]\SoundChn = PlaySound_Strict(r\NPC[0]\Sound)
								tex = LoadTexture_Strict("GFX\npcs\scientist2.jpg")
								EntityTexture r\NPC[0]\obj, tex
								FreeTexture tex
								r\NPC[0]\State=6
								PlayerRoom = r
								UnableToMove = False
								Exit
							EndIf
						Next
					EndIf
				EndIf
			EndIf
		Else
			
			temp=Infect
			Infect = Min(Infect+FPSfactor*0.004,100)
			
			If teleportForInfect
				If Infect < 94.7 Then
					EntityAlpha InfectOverlay, 0.5 * (Sin(MilliSecs()/8.0)+2.0)
					BlurTimer = 900
					
					If Infect > 94.5 Then BlinkTimer = Max(Min(-50*(Infect-94.5),BlinkTimer),-10)
					PointEntity Collider, PlayerRoom\NPC[0]\Collider
					PointEntity PlayerRoom\NPC[0]\Collider, Collider
					PointEntity Camera, PlayerRoom\NPC[0]\Collider,EntityRoll(Camera)
					ForceMove = 0.75
					Injuries = 2.5
					Bloodloss = 0
					UnableToMove = False
					
					Animate2(PlayerRoom\NPC[0]\obj, AnimTime(PlayerRoom\NPC[0]\obj), 357, 381, 0.3)
				ElseIf Infect < 98.5
					
					EntityAlpha InfectOverlay, 0.5 * (Sin(MilliSecs()/5.0)+2.0)
					BlurTimer = 950
					
					ForceMove = 0.0
					UnableToMove = True
					PointEntity Camera, PlayerRoom\NPC[0]\Collider
					
					If temp < 94.7 Then 
						PlayerRoom\NPC[0]\Sound = LoadSound_Strict("SFX\SCP\008\KillScientist2.ogg")
						PlayerRoom\NPC[0]\SoundChn = PlaySound_Strict(PlayerRoom\NPC[0]\Sound)
						
						DeathMSG = GetLocalString("Deaths", "008")
						
						Kill()
						de.Decals = CreateDecal(3, EntityX(PlayerRoom\NPC[0]\Collider), 544*RoomScale + 0.01, EntityZ(PlayerRoom\NPC[0]\Collider),90,Rnd(360),0)
						de\Size = 0.8
						ScaleSprite(de\obj, de\Size,de\Size)
					ElseIf Infect > 96
						BlinkTimer = Max(Min(-10*(Infect-96),BlinkTimer),-10)
					Else
						KillTimer = Max(-350, KillTimer)
					EndIf
					
					If PlayerRoom\NPC[0]\State2=0 Then
						Animate2(PlayerRoom\NPC[0]\obj, AnimTime(PlayerRoom\NPC[0]\obj), 13, 19, 0.3,False)
						If AnimTime(PlayerRoom\NPC[0]\obj) => 19 Then PlayerRoom\NPC[0]\State2=1
					Else
						Animate2(PlayerRoom\NPC[0]\obj, AnimTime(PlayerRoom\NPC[0]\obj), 19, 13, -0.3)
						If AnimTime(PlayerRoom\NPC[0]\obj) =< 13 Then PlayerRoom\NPC[0]\State2=0
					EndIf
					
					If ParticleAmount>0
						If Rand(50)=1 Then
							p.Particles = CreateParticle(EntityX(PlayerRoom\NPC[0]\Collider),EntityY(PlayerRoom\NPC[0]\Collider),EntityZ(PlayerRoom\NPC[0]\Collider), 5, Rnd(0.05,0.1), 0.15, 200)
							p\speed = 0.01
							p\SizeChange = 0.01
							p\A = 0.5
							p\Achange = -0.01
							RotateEntity p\pvt, Rnd(360),Rnd(360),0
						EndIf
					EndIf
					
					PositionEntity Head, EntityX(PlayerRoom\NPC[0]\Collider,True), EntityY(PlayerRoom\NPC[0]\Collider,True)+0.65,EntityZ(PlayerRoom\NPC[0]\Collider,True),True
					RotateEntity Head, (1.0+Sin(MilliSecs()/5.0))*15, PlayerRoom\angle-180, 0, True
					MoveEntity Head, 0,0,-0.4
					TurnEntity Head, 80+(Sin(MilliSecs()/5.0))*30,(Sin(MilliSecs()/5.0))*40,0
				EndIf
			Else
				Kill()
				BlinkTimer = Max(Min(-10*(Infect-96),BlinkTimer),-10)
				If PlayerRoom\RoomTemplate\Name = "dimension1499" Then
					DeathMSG = GetLocalString("Deaths", "0081499")
				ElseIf PlayerRoom\RoomTemplate\Name = "gatea" Lor (PlayerRoom\RoomTemplate\Name = "gateb" And EntityY(Collider) > 1040.0 * RoomScale) Then
					If PlayerRoom\RoomTemplate\Name = "gatea" Then
						DeathMSG = GetLocalString("Deaths", "008gatea")
					Else
						DeathMSG = GetLocalString("Deaths", "008gateb")
					EndIf
				Else
					DeathMSG = ""
				EndIf
			EndIf
		EndIf
		
		
	Else
		HideEntity InfectOverlay
	EndIf
End Function

;--------------------------------------- decals -------------------------------------------------------

Type Decals
	Field obj%
	Field SizeChange#, Size#, MaxSize#
	Field AlphaChange#, Alpha#
	Field blendmode%
	Field fx%
	Field ID%
	Field Timer#
	
	Field lifetime#
	
	Field x#, y#, z#
	Field pitch#, yaw#, roll#
End Type

Function CreateDecal.Decals(id%, x#, y#, z#, pitch#, yaw#, roll#)
	Local d.Decals = New Decals
	
	d\x = x
	d\y = y
	d\z = z
	d\pitch = pitch
	d\yaw = yaw
	d\roll = roll
	
	d\MaxSize = 1.0
	
	d\Alpha = 1.0
	d\Size = 1.0
	d\obj = CreateSprite()
	d\blendmode = 1
	
	EntityTexture(d\obj, DecalTextures(id))
	EntityFX(d\obj, 0)
	SpriteViewMode(d\obj, 2)
	PositionEntity(d\obj, x, y, z)
	RotateEntity(d\obj, pitch, yaw, roll)
	
	d\ID = id
	
	If DecalTextures(id) = 0 Lor d\obj = 0 Then Return Null
	
	Return d
End Function

Function UpdateDecals()
	Local d.Decals
	For d.Decals = Each Decals
		If d\SizeChange <> 0 Then
			d\Size=d\Size + d\SizeChange * FPSfactor
			ScaleSprite(d\obj, d\Size, d\Size)
			
			Select d\ID
				Case 0
					If d\Timer <= 0 Then
						Local angle# = Rand(360)
						Local temp# = Rnd(d\Size)
						Local d2.Decals = CreateDecal(1, EntityX(d\obj) + Cos(angle) * temp, EntityY(d\obj) - 0.0005, EntityZ(d\obj) + Sin(angle) * temp, EntityPitch(d\obj), Rnd(360), EntityRoll(d\obj))
						d2\Size = Rnd(0.1, 0.5) : ScaleSprite(d2\obj, d2\Size, d2\Size)
						PlaySound2(DecaySFX(Rand(1, 3)), Camera, d2\obj, 10.0, Rnd(0.1, 0.5))
						;d\Timer = d\Timer + Rand(50,150)
						d\Timer = Rand(50, 100)
					Else
						d\Timer= d\Timer-FPSfactor
					EndIf
				;Case 6
				;	EntityBlend d\obj, 2
			End Select
			
			If d\Size >= d\MaxSize Then d\SizeChange = 0 : d\Size = d\MaxSize
		EndIf
		
		If d\AlphaChange <> 0 Then
			d\Alpha = Min(d\Alpha + FPSfactor * d\AlphaChange, 1.0)
			EntityAlpha(d\obj, d\Alpha)
		EndIf
		
		If d\lifetime > 0 Then
			d\lifetime=Max(d\lifetime-FPSfactor,5)
		EndIf
		
		If d\Size <= 0 Lor d\Alpha <= 0 Lor d\lifetime=5.0  Then
			FreeEntity(d\obj)
			Delete d
		EndIf
	Next
End Function

;--------------------------------------- INI-functions -------------------------------------------------------

Type INIFile
	Field name$
	Field bank%
	Field bankOffset% = 0
	Field size%
End Type

Function ReadINILine$(file.INIFile)
	Local rdbyte%
	Local firstbyte% = True
	Local offset% = file\bankOffset
	Local bank% = file\bank
	Local retStr$ = ""
	rdbyte = PeekByte(bank,offset)
	While ((firstbyte) Lor ((rdbyte<>13) And (rdbyte<>10))) And (offset<file\size)
		rdbyte = PeekByte(bank,offset)
		If ((rdbyte<>13) And (rdbyte<>10)) Then
			firstbyte = False
			retStr=retStr+Chr(rdbyte)
		EndIf
		offset=offset+1
	Wend
	file\bankOffset = offset
	Return retStr
End Function

Function UpdateINIFile$(filename$)
	Local file.INIFile = Null
	For k.INIFile = Each INIFile
		If k\name = Lower(filename) Then
			file = k
		EndIf
	Next
	
	If file=Null Then Return
	
	If file\bank<>0 Then FreeBank file\bank
	Local f% = ReadFile(file\name)
	Local fleSize% = 1
	While fleSize<FileSize(file\name)
		fleSize=fleSize*2
	Wend
	file\bank = CreateBank(fleSize)
	file\size = 0
	While Not Eof(f)
		PokeByte(file\bank,file\size,ReadByte(f))
		file\size=file\size+1
	Wend
	CloseFile(f)
End Function

Function DeleteINIFile(filename$)
	If FileType(filename) <> 0 Then
		Local file.INIFile = Null
		For k.INIFile = Each INIFile
			If k\name = Lower(filename) Then
				file = k
			EndIf
		Next
		If file <> Null Then
			FreeBank file\bank
			DebugLog "FREED BANK FOR "+filename
			Delete file
			Return
		EndIf
	EndIf
	DebugLog "COULD NOT FREE BANK FOR "+filename+": INI FILE IS NOT LOADED"
End Function

Function GetINIString$(file$, section$, parameter$, defaultvalue$="")
	Local TemporaryString$ = ""
	
	Local lfile.INIFile = Null
	For k.INIFile = Each INIFile
		If k\name = Lower(file) Then
			lfile = k
		EndIf
	Next
	
	If lfile = Null Then
		DebugLog "CREATE BANK FOR "+file
		lfile = New INIFile
		lfile\name = Lower(file)
		lfile\bank = 0
		UpdateINIFile(lfile\name)
	EndIf
	
	lfile\bankOffset = 0
	
	section = Lower(section)
	
	;While Not Eof(f)
	While lfile\bankOffset<lfile\size
		Local strtemp$ = ReadINILine(lfile)
		If Left(strtemp,1) = "[" Then
			strtemp$ = Lower(strtemp)
			If Mid(strtemp, 2, Len(strtemp)-2)=section Then
				Repeat
					TemporaryString = ReadINILine(lfile)
					If Lower(Trim(Left(TemporaryString, Max(Instr(TemporaryString, "=") - 1, 0)))) = Lower(parameter) Then
						;CloseFile f
						Return Trim( Right(TemporaryString,Len(TemporaryString)-Instr(TemporaryString,"=")) )
					EndIf
				Until (Left(TemporaryString, 1) = "[") Lor (lfile\bankOffset>=lfile\size)
				
				;CloseFile f
				Return defaultvalue
			EndIf
		EndIf
	Wend
	
	Return defaultvalue
End Function

Function GetINIInt%(file$, section$, parameter$, defaultvalue% = 0)
	Local txt$ = GetINIString(file$, section$, parameter$, defaultvalue)
	If Lower(txt) = "true" Then
		Return 1
	ElseIf Lower(txt) = "false"
		Return 0
	Else
		Return Int(txt)
	EndIf
End Function

Function GetINIFloat#(file$, section$, parameter$, defaultvalue# = 0.0)
	Return Float(GetINIString(file$, section$, parameter$, defaultvalue))
End Function


Function GetINIString2$(file$, start%, parameter$, defaultvalue$="")
	Local TemporaryString$ = ""
	Local f% = ReadFile(file)
	
	Local n%=0
	While Not Eof(f)
		Local strtemp$ = ReadLine(f)
		n=n+1
		If n=start Then 
			Repeat
				TemporaryString = ReadLine(f)
				If Lower(Trim(Left(TemporaryString, Max(Instr(TemporaryString, "=") - 1, 0)))) = Lower(parameter) Then
					CloseFile f
					Return Trim( Right(TemporaryString,Len(TemporaryString)-Instr(TemporaryString,"=")) )
				EndIf
			Until Left(TemporaryString, 1) = "[" Lor Eof(f)
			CloseFile f
			Return defaultvalue
		EndIf
	Wend
	
	CloseFile f	
	
	Return defaultvalue
End Function

Function GetINIInt2%(file$, start%, parameter$, defaultvalue$="")
	Local txt$ = GetINIString2(file$, start%, parameter$, defaultvalue$)
	If Lower(txt) = "true" Then
		Return 1
	ElseIf Lower(txt) = "false"
		Return 0
	Else
		Return Int(txt)
	EndIf
End Function


Function GetINISectionLocation%(file$, section$)
	Local Temp%
	Local f% = ReadFile(file)
	
	section = Lower(section)
	
	Local n%=0
	While Not Eof(f)
		Local strtemp$ = ReadLine(f)
		n=n+1
		If Left(strtemp,1) = "[" Then
			strtemp$ = Lower(strtemp)
			Temp = Instr(strtemp, section)
			If Temp>0 Then
				If Mid(strtemp, Temp-1, 1)="[" Lor Mid(strtemp, Temp-1, 1)="|" Then
					CloseFile f
					Return n
				EndIf
			EndIf
		EndIf
	Wend
	
	CloseFile f
End Function



Function PutINIValue%(file$, INI_sSection$, INI_sKey$, INI_sValue$)
	
	; Returns: True (Success) Lor False (Failed)
	
	INI_sSection = "[" + Trim$(INI_sSection) + "]"
	Local INI_sUpperSection$ = Upper$(INI_sSection)
	INI_sKey = Trim$(INI_sKey)
	INI_sValue = Trim$(INI_sValue)
	Local INI_sFilename$ = file$
	
	; Retrieve the INI Data (If it exists)
	
	Local INI_sContents$ = INI_FileToString(INI_sFilename)
	
		; (Re)Create the INI file updating/adding the SECTION, KEY And VALUE
	
	Local INI_bWrittenKey% = False
	Local INI_bSectionFound% = False
	Local INI_sCurrentSection$ = ""
	
	Local INI_lFileHandle% = WriteFile(INI_sFilename)
	If INI_lFileHandle = 0 Then Return False ; Create file failed!
	
	Local INI_lOldPos% = 1
	Local INI_lPos% = Instr(INI_sContents, Chr$(0))
	
	While (INI_lPos <> 0)
		
		Local INI_sTemp$ = Mid$(INI_sContents, INI_lOldPos, (INI_lPos - INI_lOldPos))
		
		If (INI_sTemp <> "") Then
			
			If Left$(INI_sTemp, 1) = "[" And Right$(INI_sTemp, 1) = "]" Then
				
					; Process SECTION
				
				If (INI_sCurrentSection = INI_sUpperSection) And (INI_bWrittenKey = False) Then
					INI_bWrittenKey = INI_CreateKey(INI_lFileHandle, INI_sKey, INI_sValue)
				EndIf
				INI_sCurrentSection = Upper$(INI_CreateSection(INI_lFileHandle, INI_sTemp))
				If (INI_sCurrentSection = INI_sUpperSection) Then INI_bSectionFound = True
				
			Else
				If Left(INI_sTemp, 1) = ":" Then
					WriteLine INI_lFileHandle, INI_sTemp
				Else
						; KEY=VALUE				
					Local lEqualsPos% = Instr(INI_sTemp, "=")
					If (lEqualsPos <> 0) Then
						If (INI_sCurrentSection = INI_sUpperSection) And (Upper$(Trim$(Left$(INI_sTemp, (lEqualsPos - 1)))) = Upper$(INI_sKey)) Then
							If (INI_sValue <> "") Then INI_CreateKey INI_lFileHandle, INI_sKey, INI_sValue
							INI_bWrittenKey = True
						Else
							WriteLine INI_lFileHandle, INI_sTemp
						EndIf
					EndIf
				EndIf
				
			EndIf
			
		EndIf
		
			; Move through the INI file...
		
		INI_lOldPos = INI_lPos + 1
		INI_lPos% = Instr(INI_sContents, Chr$(0), INI_lOldPos)
		
	Wend
	
		; KEY wasn;t found in the INI file - Append a New SECTION If required And create our KEY=VALUE Line
	
	If (INI_bWrittenKey = False) Then
		If (INI_bSectionFound = False) Then INI_CreateSection INI_lFileHandle, INI_sSection
		INI_CreateKey INI_lFileHandle, INI_sKey, INI_sValue
	EndIf
	
	CloseFile INI_lFileHandle
	
	Return True ; Success
	
End Function

Function INI_FileToString$(INI_sFilename$)
	
	Local INI_sString$ = ""
	Local INI_lFileHandle%= ReadFile(INI_sFilename)
	If INI_lFileHandle <> 0 Then
		While Not(Eof(INI_lFileHandle))
			INI_sString = INI_sString + ReadLine$(INI_lFileHandle) + Chr$(0)
		Wend
		CloseFile INI_lFileHandle
	EndIf
	Return INI_sString
	
End Function

Function INI_CreateSection$(INI_lFileHandle%, INI_sNewSection$)
	
	If FilePos(INI_lFileHandle) <> 0 Then WriteLine INI_lFileHandle, "" ; Blank Line between sections
	WriteLine INI_lFileHandle, INI_sNewSection
	Return INI_sNewSection
	
End Function

Function INI_CreateKey%(INI_lFileHandle%, INI_sKey$, INI_sValue$)
	
	WriteLine INI_lFileHandle, INI_sKey + " = " + INI_sValue
	Return True
	
End Function

Function GetFileAmount(ReadDir$, OnlyFolders%=False)
	Local Amount% = 0
	Local Dir% = ReadDir(ReadDir)
	Local File$
	NextFile(Dir) : NextFile(Dir)
	Repeat
		File = NextFile$(Dir)
		If File = "" Then Exit
		If ((Not OnlyFolders) Lor FileType(ReadDir + File) = 2) Then Amount = Amount + 1
	Forever
	CloseDir Dir
	DebugLog Amount
	Return Amount
End Function

;Save options to .ini.
Function SaveOptionsINI()
	
	PutINIValue(OptionFile, "options", "mouse sensitivity", MouseSens)
	PutINIValue(OptionFile, "options", "invert mouse y", InvertMouse)
	PutINIValue(OptionFile, "options", "bump mapping enabled", BumpEnabled)			
	PutINIValue(OptionFile, "options", "HUD enabled", HUDenabled)
	PutINIValue(OptionFile, "options", "screengamma", ScreenGamma)
	PutINIValue(OptionFile, "options", "antialias", Opt_AntiAlias)
	PutINIValue(OptionFile, "options", "vsync", Vsync)
	PutINIValue(OptionFile, "options", "show FPS", I_Opt\ShowFPS)
	PutINIValue(OptionFile, "options", "framelimit", Framelimit%)
	PutINIValue(OptionFile, "options", "achievement popup enabled", AchvMSGenabled%)
	PutINIValue(OptionFile, "options", "room lights enabled", EnableRoomLights%)
	PutINIValue(OptionFile, "options", "texture details", TextureDetails%)
	PutINIValue(OptionFile, "options", "particle amount", ParticleAmount)
	PutINIValue(OptionFile, "options", "enable vram", EnableVRam)
	PutINIValue(OptionFile, "options", "mouse smoothing", MouseSmooth)
	PutINIValue(OptionFile, "options", "fov", FOV)
	PutINIValue(OptionFile, "options", "launcher enabled", I_Opt\LauncherEnabled%)
	PutINIValue(OptionFile, "console", "enabled", I_Opt\ConsoleEnabled%)
	PutINIValue(OptionFile, "console", "auto opening", I_Opt\ConsoleOnError%)
	
	PutINIValue(OptionFile, "audio", "music volume", MusicVolume)
	PutINIValue(OptionFile, "audio", "sound volume", PrevSFXVolume)
	PutINIValue(OptionFile, "audio", "sfx release", EnableSFXRelease)
	PutINIValue(OptionFile, "audio", "enable user tracks", EnableUserTracks%)
	PutINIValue(OptionFile, "audio", "user track setting", UserTrackMode%)
	
	PutINIValue(OptionFile, "binds", "Right key", I_Keys\Right)
	PutINIValue(OptionFile, "binds", "Left key", I_Keys\Left)
	PutINIValue(OptionFile, "binds", "Up key", I_Keys\UP)
	PutINIValue(OptionFile, "binds", "Down key", I_Keys\DOWN)
	PutINIValue(OptionFile, "binds", "Blink key", I_Keys\BLINK)
	PutINIValue(OptionFile, "binds", "Sprint key", I_Keys\SPRINT)
	PutINIValue(OptionFile, "binds", "Inventory key", I_Keys\INV)
	PutINIValue(OptionFile, "binds", "Crouch key", I_Keys\CROUCH)
	PutINIValue(OptionFile, "binds", "Save key", I_Keys\SAVE)
	PutINIValue(OptionFile, "binds", "Console key", I_Keys\CONSOLE)
	
End Function

Function DefaultOptionsINI()
	
	PutINIValue(OptionFile, "options", "width", DesktopWidth())
	PutINIValue(OptionFile, "options", "height", DesktopHeight())
	PutINIValue(OptionFile, "options", "mode", 0)
	PutINIValue(OptionFile, "options", "audio driver", 0)
	PutINIValue(OptionFile, "options", "brightness", 50)
	PutINIValue(OptionFile, "options", "screengamma", 1.0)
	PutINIValue(OptionFile, "options", "show FPS", 0)
	PutINIValue(OptionFile, "options", "framelimit", 0)
	PutINIValue(OptionFile, "options", "vsync", 1)
	PutINIValue(OptionFile, "options", "mouse sensitivity", 0.0)
	PutINIValue(OptionFile, "options", "invert mouse y", 0)
	PutINIValue(OptionFile, "options", "mouse smoothing", 1.0)
	PutINIValue(OptionFile, "options", "achievement popup enabled", 1)
	PutINIValue(OptionFile, "options", "bump mapping enabled", 1)
	PutINIValue(OptionFile, "options", "antialias", 1)
	PutINIValue(OptionFile, "options", "hud enabled", 1)
	PutINIValue(OptionFile, "options", "intro enabled", 1)
	PutINIValue(OptionFile, "options", "room lights enabled", 1)
	PutINIValue(OptionFile, "options", "texture details", 3)
	PutINIValue(OptionFile, "options", "antialiased text", 0)
	PutINIValue(OptionFile, "options", "particle amount", 2)
	PutINIValue(OptionFile, "options", "fov", 60)
	PutINIValue(OptionFile, "options", "enable vram", 0)
	PutINIValue(OptionFile, "options", "play startup video", 1)
	PutINIValue(OptionFile, "options", "pack", "English")
	PutINIValue(OptionFile, "options", "launcher enabled", 0)
	
	PutINIValue(OptionFile, "audio", "music volume", 0.5)
	PutINIValue(OptionFile, "audio", "sound volume", 1.0)
	PutINIValue(OptionFile, "audio", "enable user tracks", 1)
	PutINIValue(OptionFile, "audio", "user track setting", 1)
	PutINIValue(OptionFile, "audio", "sfx release", 1)
	
	PutINIValue(OptionFile, "binds", "right key", 32)
	PutINIValue(OptionFile, "binds", "left key", 30)
	PutINIValue(OptionFile, "binds", "up key", 17)
	PutINIValue(OptionFile, "binds", "down key", 31)
	PutINIValue(OptionFile, "binds", "blink key", 57)
	PutINIValue(OptionFile, "binds", "sprint key", 42)
	PutINIValue(OptionFile, "binds", "inventory key", 15)
	PutINIValue(OptionFile, "binds", "crouch key", 29)
	PutINIValue(OptionFile, "binds", "save key", 63)
	PutINIValue(OptionFile, "binds", "console key", 61)
	
	PutINIValue(OptionFile, "console", "enabled", 0)
	PutINIValue(OptionFile, "console", "auto opening", 0)
	
End Function

; Find mesh extents
Function GetMeshExtents(Mesh%)
	Local s%, surf%, surfs%, v%, verts%, x#, y#, z#
	Local minx# = INFINITY
	Local miny# = INFINITY
	Local minz# = INFINITY
	Local maxx# = -INFINITY
	Local maxy# = -INFINITY
	Local maxz# = -INFINITY
	
	surfs = CountSurfaces(Mesh)
	
	For s = 1 To surfs
		surf = GetSurface(Mesh, s)
		verts = CountVertices(surf)
		
		For v = 0 To verts - 1
			x = VertexX(surf, v)
			y = VertexY(surf, v)
			z = VertexZ(surf, v)
			
			If (x < minx) Then minx = x
			If (x > maxx) Then maxx = x
			If (y < miny) Then miny = y
			If (y > maxy) Then maxy = y
			If (z < minz) Then minz = z
			If (z > maxz) Then maxz = z
		Next
	Next
	
	Mesh_MinX = minx
	Mesh_MinY = miny
	Mesh_MinZ = minz
	Mesh_MaxX = maxx
	Mesh_MaxY = maxy
	Mesh_MaxZ = maxz
	Mesh_MagX = maxx-minx
	Mesh_MagY = maxy-miny
	Mesh_MagZ = maxz-minz
	
End Function

;Floats so it doesn't need to be cast
Global SMALLEST_POWER_TWO#
Global SMALLEST_POWER_TWO_HALF#

Function Graphics3DExt%(width%,height%,mode%=2)
	Graphics3D width,height,32,mode
	TextureFilter "", 8192 ;This turns on Anisotropic filtering for textures. Use TextureAnisotropic to change anisotropic level.
	TextureAnisotropic 16
	SMALLEST_POWER_TWO = 512
	While SMALLEST_POWER_TWO < width Lor SMALLEST_POWER_TWO < height
		SMALLEST_POWER_TWO = SMALLEST_POWER_TWO * 2
	Wend
	SMALLEST_POWER_TWO_HALF = SMALLEST_POWER_TWO / 2
	InitFastResize()
	AntiAlias GetINIInt(OptionFile,"options","antialias")
End Function

Function ResizeImage2(image%,width%,height%)
	img% = CreateImage(width,height)
	
	oldWidth% = ImageWidth(image)
	oldHeight% = ImageHeight(image)
	CopyRect 0,0,oldWidth,oldHeight,1024-oldWidth/2,1024-oldHeight/2,ImageBuffer(image),TextureBuffer(fresize_texture)
	SetBuffer BackBuffer()
	ScaleRender(0,0,2048.0 / Float(RealGraphicWidth) * Float(width) / Float(oldWidth), 2048.0 / Float(RealGraphicWidth) * Float(height) / Float(oldHeight))
	;might want to replace Float(I_Opt\GraphicWidth) with Max(I_Opt\GraphicWidth,I_Opt\GraphicHeight) if portrait sizes cause issues
	;everyone uses landscape so it's probably a non-issue
	CopyRect RealGraphicWidth/2-width/2,RealGraphicHeight/2-height/2,width,height,0,0,BackBuffer(),ImageBuffer(img)
	
	FreeImage image
	Return img
End Function


Function RenderWorld2(tween#)
	
	CameraProjMode ark_blur_cam,0
	CameraProjMode Camera,1
	
	If WearingNightVision>0 And WearingNightVision<3 Then
		AmbientLight Min(Brightness*2,255), Min(Brightness*2,255), Min(Brightness*2,255)
	ElseIf WearingNightVision=3
		AmbientLight 255,255,255
	ElseIf PlayerRoom<>Null
		If (PlayerRoom\RoomTemplate\Name<>"room173") And (PlayerRoom\RoomTemplate\Name<>"gateb" And EntityY(Collider) =< 1040.0 * RoomScale) And (PlayerRoom\RoomTemplate\Name<>"gatea") Then
			AmbientLight Brightness, Brightness, Brightness
		EndIf
	EndIf
	
	IsNVGBlinking% = False
	
	CameraViewport Camera,0,0,I_Opt\GraphicWidth,I_Opt\GraphicHeight
	
	Local hasBattery%
	Local power%
	If (WearingNightVision<>0 And WearingNightVision<>3) Lor WearingScramble<>0 Then
		For i% = 0 To MaxItemAmount - 1
			If Inventory(i)<>Null And Inventory(i)\picked = 2 And (Inventory(i)\itemtemplate\tempname = "badnvg" Lor Inventory(i)\itemtemplate\tempname = "nvg" Lor Inventory(i)\itemtemplate\tempname = "supernvg" Lor Inventory(i)\itemtemplate\tempname = "scramble") Then
				Inventory(i)\state = Max(0, Inventory(i)\state - (FPSfactor * ((0.02 * Abs(WearingNightVision)) + (0.25 * (WearingScramble <> 0)) - (0.1 * (WearingScramble = 2)))))
				power%=Int(Inventory(i)\state)
				If power=0.0 Then ;this nvg can't be used
					hasBattery = 0
					If WearingScramble=0 Then
						Msg = GetLocalString("Messages", "nvgempty")
						MsgTimer = 350
						If WearingNightVision<>-1 Then
							IsNVGBlinking = True
							BlinkTimer = -1.0
						EndIf
					EndIf
				ElseIf power<=100.0 Then
					hasBattery = 1
				Else
					hasBattery = 2
				EndIf
				Exit
			EndIf
		Next
		
		If WearingNightVision=-1 Then
			If hasBattery > 0 Then
				Msg = GetLocalString("Messages", "novg")
				MsgTimer = 350
				BlinkTimer = -1.0
				IsNVGBlinking% = True
			EndIf
		ElseIf WearingNightVision = 2 Then
			If NVTimer<=0.0 Then
				For np.NPCs = Each NPCs
					np\NVX = EntityX(np\Collider,True)
					np\NVY = EntityY(np\Collider,True)
					np\NVZ = EntityZ(np\Collider,True)
				Next
				If NVTimer<=-10
					NVTimer = 600.0
				EndIf
				IsNVGBlinking% = True
			EndIf
			
			NVTimer=NVTimer-FPSfactor
		EndIf
	EndIf
	
	If (Not IsNVGBlinking) Then RenderWorld(tween)
	
	CurrTrisAmount = TrisRendered()
	
	ScrambleActive = 0
	
	If WearingScramble <> 0 Then
		If hasBattery = 0 
			If (MilliSecs() Mod 400) < 200 Then
				Color 255,0,0
				SetFont I_Opt\Fonts[3]
				Text I_Opt\GraphicWidth/2,20*MenuScale,GetLocalString("Game", "nobat"),True,False
			EndIf
			If ChannelPlaying(ScrambleCHN) Then StopChannel(ScrambleCHN)
		Else
			ScrambleActive = WearingScramble Mod 100
			ScrambleCHN = LoopSound2(ScrambleSFX, ScrambleCHN, Camera, Camera)
		EndIf
	Else
		If ChannelPlaying(ScrambleCHN) Then StopChannel(ScrambleCHN)
	EndIf
	
	If hasBattery > 0 Then
		If WearingNightVision=2 Then ;show a HUD
			
			Color 255,255,255
			
			SetFont I_Opt\Fonts[3]
			
			Local plusY% = 0
			If hasBattery=1 Then plusY% = 40
			
			Text I_Opt\GraphicWidth/2,(20+plusY)*MenuScale,GetLocalString("Game", "nvgrefresh"),True,False
			
			Text I_Opt\GraphicWidth/2,(60+plusY)*MenuScale,Max(f2s(NVTimer/60.0,1),0.0),True,False
			Text I_Opt\GraphicWidth/2,(100+plusY)*MenuScale,GetLocalString("Game", "nvgseconds"),True,False
			
			temp% = CreatePivot() : temp2% = CreatePivot()
			PositionEntity temp, EntityX(Collider), EntityY(Collider), EntityZ(Collider)
			
			Color 255,255,255;*(NVTimer/600.0)
			
			For np.NPCs = Each NPCs
				If np\NVName<>"" And (Not np\HideFromNVG) Then ;don't waste your time if the string is empty
					PositionEntity temp2,np\NVX,np\NVY,np\NVZ
					
					Local Dist# = EntityDistanceSquared(Temp2, Collider)
					
					If Dist < PowTwo(23.5) Then ;don't draw text if the NPC is too far away
						PointEntity temp, temp2
						yawvalue# = WrapAngle(EntityYaw(Camera) - EntityYaw(temp))
						xvalue# = 0.0
						If yawvalue > 90 And yawvalue <= 180 Then
							xvalue# = Sin(90)/90*yawvalue
						ElseIf yawvalue > 180 And yawvalue < 270 Then
							xvalue# = Sin(270)/yawvalue*270
						Else
							xvalue = Sin(yawvalue)
						EndIf
						pitchvalue# = WrapAngle(EntityPitch(Camera) - EntityPitch(temp))
						yvalue# = 0.0
						If pitchvalue > 90 And pitchvalue <= 180 Then
							yvalue# = Sin(90)/90*pitchvalue
						ElseIf pitchvalue > 180 And pitchvalue < 270 Then
							yvalue# = Sin(270)/pitchvalue*270
						Else
							yvalue# = Sin(pitchvalue)
						EndIf
						
						If (Not IsNVGBlinking%)
							Text I_Opt\GraphicWidth / 2 + xvalue * (I_Opt\GraphicWidth / 2),I_Opt\GraphicHeight / 2 - yvalue * (I_Opt\GraphicHeight / 2),np\NVName,True,True
							Text I_Opt\GraphicWidth / 2 + xvalue * (I_Opt\GraphicWidth / 2),I_Opt\GraphicHeight / 2 - yvalue * (I_Opt\GraphicHeight / 2) + 30.0 * MenuScale,f2s(Sqr(Dist),1)+" m",True,True
						EndIf
					EndIf
				EndIf
			Next
			
			FreeEntity (temp) : FreeEntity (temp2)
			
			Color 0,0,55
		ElseIf WearingNightVision=1
			Color 0,55,0
		Else ;SCRAMBLE or NoVG
			Color 55,55,55
		EndIf
		For k=0 To 10
			Rect 45,I_Opt\GraphicHeight*0.5-(k*20),54,10,True
		Next
		If WearingNightVision = 2
			Color(0, 0, 255)
		ElseIf WearingNightVision = 1
			Color(0, 255, 0)
		Else ;SCRAMBLE or NoVG
			Color(255, 255, 255)
		EndIf
		For l=0 To Min(Floor((power%+50)*0.01), 11)
			Rect 45,I_Opt\GraphicHeight*0.5-(l*20),54,10,True
		Next
		DrawImage NVGImages,40,I_Opt\GraphicHeight*0.5+30
		
		If hasBattery = 1 And ((MilliSecs() Mod 800) < 400) Then
			Color 255,0,0
			SetFont I_Opt\Fonts[3]
			
			Text I_Opt\GraphicWidth/2,20*MenuScale,GetLocalString("Game", "lowbat"),True,False
		EndIf
		Color 255,255,255
	EndIf
	
	;render sprites
	CameraProjMode ark_blur_cam,2
	CameraProjMode Camera,0
	RenderWorld()
	CameraProjMode ark_blur_cam,0
	
End Function


Function ScaleRender(x#,y#,hscale#=1.0,vscale#=1.0)
	If Camera<>0 Then HideEntity Camera
	WireFrame 0
	ShowEntity fresize_image
	ScaleEntity fresize_image,hscale,vscale,1.0
	PositionEntity fresize_image, x, y, 1.0001
	ShowEntity fresize_cam
	RenderWorld()
	HideEntity fresize_cam
	HideEntity fresize_image
	WireFrame = I_Cheats\WireframeState
	If Camera<>0 Then ShowEntity Camera
End Function

Function InitFastResize()
	;Create Camera
	Local cam% = CreateCamera()
	CameraProjMode cam, 2
	CameraZoom cam, 0.1
	CameraClsMode cam, 0, 0
	CameraRange cam, 0.1, 1.5
	MoveEntity cam, 0, 0, -10000
	
	fresize_cam = cam
	
	;ark_sw = GraphicsWidth()
	;ark_sh = GraphicsHeight()
	
	;Create sprite
	Local spr% = CreateMesh(cam)
	Local sf% = CreateSurface(spr)
	AddVertex sf, -1, 1, 0, 0, 0
	AddVertex sf, 1, 1, 0, 1, 0
	AddVertex sf, -1, -1, 0, 0, 1
	AddVertex sf, 1, -1, 0, 1, 1
	AddTriangle sf, 0, 1, 2
	AddTriangle sf, 3, 2, 1
	EntityFX spr, 17
	ScaleEntity spr, 2048.0 / Float(RealGraphicWidth), 2048.0 / Float(RealGraphicHeight), 1
	PositionEntity spr, 0, 0, 1.0001
	EntityOrder spr, -100001
	EntityBlend spr, 1
	fresize_image = spr
	
	;Create texture
	fresize_texture = CreateTexture(SMALLEST_POWER_TWO, SMALLEST_POWER_TWO, 1+256)
	fresize_texture2 = CreateTexture(SMALLEST_POWER_TWO, SMALLEST_POWER_TWO, 1+256)
	TextureBlend fresize_texture2,3
	SetBuffer(TextureBuffer(fresize_texture2))
	ClsColor 0,0,0
	Cls
	SetBuffer(BackBuffer())
	;TextureAnisotropy(fresize_texture)
	EntityTexture spr, fresize_texture,0,0
	EntityTexture spr, fresize_texture2,0,1
	
	HideEntity fresize_cam
End Function


Function UpdateLeave1499()
	Local r.Rooms, it.Items,r2.Rooms,i%
	Local r1499.Rooms
	
	If (Not Wearing1499) And PlayerRoom\RoomTemplate\Name$ = "dimension1499"
		For r.Rooms = Each Rooms
			If r = NTF_1499PrevRoom
				BlinkTimer = -1
				NTF_1499X# = EntityX(Collider)
				NTF_1499Y# = EntityY(Collider)
				NTF_1499Z# = EntityZ(Collider)
				PositionEntity (Collider, NTF_1499PrevX#, NTF_1499PrevY#+0.05, NTF_1499PrevZ#)
				ResetEntity(Collider)
				PlayerRoom = r
				UpdateDoors()
				UpdateRooms()
				If PlayerRoom\RoomTemplate\Name = "room3storage"
					If EntityY(Collider)<-4600*RoomScale
						For i = 0 To 2
							PlayerRoom\NPC[i]\State = 2
							PositionEntity(PlayerRoom\NPC[i]\Collider, EntityX(PlayerRoom\Objects[PlayerRoom\NPC[i]\State2],True),EntityY(PlayerRoom\Objects[PlayerRoom\NPC[i]\State2],True)+0.2,EntityZ(PlayerRoom\Objects[PlayerRoom\NPC[i]\State2],True))
							ResetEntity PlayerRoom\NPC[i]\Collider
							PlayerRoom\NPC[i]\State2 = PlayerRoom\NPC[i]\State2 + 1
							If PlayerRoom\NPC[i]\State2 > PlayerRoom\NPC[i]\PrevState Then PlayerRoom\NPC[i]\State2 = (PlayerRoom\NPC[i]\PrevState-3)
						Next
					EndIf
				ElseIf PlayerRoom\RoomTemplate\Name = "pocketdimension"
					CameraFogColor Camera, 0,0,0
					CameraClsColor Camera, 0,0,0
				EndIf
				For r2.Rooms = Each Rooms
					If r2\RoomTemplate\Name = "dimension1499"
						r1499 = r2
						Exit
					EndIf
				Next
				For it.Items = Each Items
					it\disttimer = 0
					If it\itemtemplate\tempname = "bad1499" Lor it\itemtemplate\tempname = "scp1499" Lor it\itemtemplate\tempname = "super1499" Lor it\itemtemplate\tempname = "fine1499"
						If EntityY(it\collider) >= EntityY(r1499\obj)-5
							PositionEntity it\collider,NTF_1499PrevX#,NTF_1499PrevY#+(EntityY(it\collider)-EntityY(r1499\obj)),NTF_1499PrevZ#
							ResetEntity it\collider
							Exit
						EndIf
					EndIf
				Next
				r1499 = Null
				ShouldEntitiesFall = False
				PlaySound_Strict (LoadTempSound("SFX\SCP\1499\Exit.ogg"))
				NTF_1499PrevX# = 0.0
				NTF_1499PrevY# = 0.0
				NTF_1499PrevZ# = 0.0
				NTF_1499PrevRoom = Null
				Exit
			EndIf
		Next
	EndIf
	
End Function

Function CheckForPlayerInFacility()
	;False (=0): NPC is not in facility (mostly meant for "dimension1499")
	;True (=1): NPC is in facility
	;2: NPC is in tunnels (maintenance tunnels/049 tunnels/939 storage room, etc...)
	
	If EntityY(Collider)>100.0
		Return False
	EndIf
	If EntityY(Collider) < -10.0 Lor EntityY(Collider) > 7.0
		Return 2
	EndIf
	
	Return True
End Function

Function ScaledMouseX%()
	Return Float(MouseX()-(RealGraphicWidth*0.5*(1.0-AspectRatioRatio)))*Float(I_Opt\GraphicWidth)/Float(RealGraphicWidth*AspectRatioRatio)
End Function

Function ScaledMouseY%()
	Return Float(MouseY())*Float(I_Opt\GraphicHeight)/Float(RealGraphicHeight)
End Function

Function TeleportEntity(entity%,x#,y#,z#,customradius#=0.3,isglobal%=False,pickrange#=2.0,dir%=0)
	Local pvt,pick
	;dir = 0 - towards the floor (default)
	;dir = 1 - towrads the ceiling (mostly for PD decal after leaving dimension)
	
	pvt = CreatePivot()
	PositionEntity(pvt, x,y+0.05,z,isglobal)
	If dir%=0
		RotateEntity pvt,90,0,0
	Else
		RotateEntity pvt,-90,0,0
	EndIf
	pick = EntityPick(pvt,pickrange)
	If pick<>0
		If dir%=0
			PositionEntity(entity, x,PickedY()+customradius#+0.02,z,isglobal)
		Else
			PositionEntity(entity, x,PickedY()+customradius#-0.02,z,isglobal)
		EndIf
		DebugLog "Entity teleported successfully"
	Else
		PositionEntity(entity,x,y,z,isglobal)
		DebugLog "Warning: no ground found when teleporting an entity"
	EndIf
	FreeEntity pvt
	ResetEntity entity
	DebugLog "Teleported entity to: "+EntityX(entity)+"/"+EntityY(entity)+"/"+EntityZ(entity)
	
End Function

Function PlayStartupVideos()
	
	HidePointer()
	
	Local ScaledGraphicHeight%
	Local Ratio# = Float(RealGraphicWidth)/Float(RealGraphicHeight)
	If Ratio>1.76 And Ratio<1.78
		ScaledGraphicHeight = RealGraphicHeight
		DebugLog "Not Scaled"
	Else
		ScaledGraphicHeight% = Float(RealGraphicWidth)/(16.0/9.0)
		DebugLog "Scaled: "+ScaledGraphicHeight
	EndIf
	
	Local i, moviefile$
	For i = 0 To 1
		Select i
			Case 0
				moviefile$ = "GFX\menu\startup_Undertow"
			Case 1
				moviefile$ = "GFX\menu\startup_TSS"
		End Select
		Local SplashScreenVideo = BlitzMovie_OpenD3D(moviefile$+".avi", SystemProperty("Direct3DDevice7"), SystemProperty("DirectDraw7"))
		If SplashScreenVideo = 0 Then
			PutINIValue(OptionFile, "options", "play startup video", "false")
			Return
		EndIf
		SplashScreenVideo = BlitzMovie_Play()
		Local SplashScreenAudio = StreamSound_Strict(moviefile$+".ogg",SFXVolume,0)
		Repeat
			Cls
			BlitzMovie_DrawD3D(0, (RealGraphicHeight/2-ScaledGraphicHeight/2), RealGraphicWidth, ScaledGraphicHeight)
			Flip
		Until (GetKey() Lor (Not IsStreamPlaying_Strict(SplashScreenAudio)))
		StopStream_Strict(SplashScreenAudio)
		BlitzMovie_Stop()
		BlitzMovie_Close()
		
		Cls
		Flip
	Next
	
	ShowPointer()
	
End Function



; TODO potentially make it more unique, with an exception for all items
Function CanUseItem(canUseWithGasMask%, canUseWithEyewear%)
	If (canUseWithGasMask = False And ((WearingGasMask <> 0) Lor (Wearing1499 <> 0)))
		Msg = GetLocalString("Messages", "cantgas")
		MsgTimer = 70*5
		Return False
	ElseIf (canUseWithEyewear = False And (WearingNightVision <> 0 Lor Wearing178 <> 0 Lor WearingScramble <> 0))
		Msg = GetLocalString("Messages", "canthead")
		MsgTimer = 70*5
		Return False
	EndIf
	
	Return True
End Function

Function ResetInput()
	
	FlushKeys()
	FlushMouse()
	MouseHit1 = 0
	MouseHit2 = 0
	MouseDown1 = 0
	MouseUp1 = 0
	MouseHit(1)
	MouseHit(2)
	MouseDown(1)
	GrabbedEntity = 0
	Input_ResetTime# = 10.0
	
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D