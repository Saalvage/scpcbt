
Function SaveGame(file$)
	;CatchErrors("SaveGame")
	
	Local I_Cheats.Cheats = First Cheats
	Local I_Loc.Loc = First Loc
	
	If Not Playable Then Return ;don't save if the player can't move at all
	
	If DropSpeed#>0.02*FPSfactor Lor DropSpeed#<-0.02*FPSfactor Then Return
	
	If KillTimer < 0 Then Return
	
	GameSaved = True
	
	Local x%, y%, i%, temp%
	Local n.NPCs, r.Rooms, do.Doors
	
	CreateDir(file)
	
	Local f% = WriteFile(file + "save.txt")
	
	WriteString f, CurrentTime()
	WriteString f, CurrentDate()
	
	WriteInt f, PlayTime
	
	WriteFloat f, EntityX(Collider)
	WriteFloat f, EntityY(Collider)
	WriteFloat f, EntityZ(Collider)
	
	WriteFloat f, EntityX(Head)
	WriteFloat f, EntityY(Head)
	WriteFloat f, EntityZ(Head)
	
	WriteString f, Str(AccessCode)
	
	WriteFloat f, EntityPitch(Collider)
	WriteFloat f, EntityYaw(Collider)
	
	WriteString f, CompatibleNumber
	
	WriteString f, I_Loc\Lang
	
	WriteFloat f, BlinkTimer
	WriteFloat f, BlinkEffect
	WriteFloat f, BlinkEffectTimer
	
	WriteInt f, DeathTimer
	WriteInt f, BlurTimer
	WriteFloat f, HealTimer
	
	WriteByte f, Crouch
	
	WriteFloat f, Stamina
	WriteFloat f, StaminaEffect
	WriteFloat f, StaminaEffectTimer
	
	WriteFloat f, EyeStuck	
	WriteFloat f, EyeIrritation
	
	WriteFloat f, Injuries
	WriteFloat f, Bloodloss
	
	WriteFloat f,PrevInjuries
	WriteFloat f,PrevBloodloss
	
	WriteString f, DeathMSG
	
	For i = 0 To 7
		WriteFloat f, SCP1025state[i]
	Next
	
	WriteFloat f, VomitTimer
	WriteByte f, Vomit
	WriteFloat f, CameraShakeTimer
	WriteFloat f, Infect
	
	For i = 0 To CUSTOM
		If (SelectedDifficulty = difficulties[i]) Then
			WriteByte f, i
			
			If (i = CUSTOM) Then
				WriteByte f,SelectedDifficulty\aggressiveNPCs
				WriteByte f,SelectedDifficulty\permaDeath
				WriteByte f,SelectedDifficulty\saveType
				WriteByte f,SelectedDifficulty\otherFactors
			EndIf
		EndIf
	Next
	
	WriteFloat f, MonitorTimer
	
	WriteFloat f, Sanity
	
	WriteByte f, WearingGasMask
	WriteByte f, WearingVest
	WriteByte f, WearingHazmat
	
	WriteByte f, WearingNightVision
	WriteByte f, Wearing1499
	WriteFloat f,NTF_1499PrevX#
	WriteFloat f,NTF_1499PrevY#
	WriteFloat f,NTF_1499PrevZ#
	WriteFloat f,NTF_1499X#
	WriteFloat f,NTF_1499Y#
	WriteFloat f,NTF_1499Z#
	If NTF_1499PrevRoom <> Null
		WriteFloat f,NTF_1499PrevRoom\x
		WriteFloat f,NTF_1499PrevRoom\z
	Else
		WriteFloat f,0.0
		WriteFloat f,0.0
	EndIf
	
	WriteByte f, I_Cheats\SuperMan
	WriteFloat f, I_Cheats\SuperManTimer
	WriteByte f, LightsOn
	
	WriteString f, RandomSeed
	
	WriteFloat f, SecondaryLightOn
	WriteFloat f, PrevSecondaryLightOn
	WriteByte f, RemoteDoorOn
	WriteByte f, SoundTransmission
	WriteByte f, Contained106
	
	For i = 0 To MAXACHIEVEMENTS-1
		WriteByte f, Achievements[i]
	Next
	WriteInt f, RefinedItems
	
	For x = 0 To MapWidth
		For y = 0 To MapHeight
			WriteInt f, MapTemp(x, y)
			WriteByte f, MapFound(x, y)
		Next
	Next
	
	WriteInt f, 113
	
	temp = 0
	For  n.NPCs = Each NPCs
		temp = temp +1
	Next	
	
	WriteInt f, temp
	For n.NPCs = Each NPCs
		DebugLog("Saving NPC " +n\NVName+ " (ID "+n\ID+")")
		
		WriteByte f, n\NPCtype
		WriteFloat f, EntityX(n\Collider,True)
		WriteFloat f, EntityY(n\Collider,True)
		WriteFloat f, EntityZ(n\Collider,True)
		
		WriteFloat f, EntityPitch(n\Collider)
		WriteFloat f, EntityYaw(n\Collider)
		WriteFloat f, EntityRoll(n\Collider)
		
		WriteFloat f, n\State
		WriteFloat f, n\State2
		WriteFloat f, n\State3
		WriteInt f, n\PrevState
		
		WriteByte f, n\Idle
		WriteFloat f, n\LastDist
		WriteInt f, n\LastSeen
		
		WriteInt f, n\CurrSpeed
		
		WriteFloat f, n\Angle
		
		WriteFloat f, n\Reload
		
		WriteInt f, n\ID
		If n\Target <> Null Then
			WriteInt f, n\Target\ID		
		Else
			WriteInt f, 0
		EndIf
		
		WriteFloat f, n\EnemyX
		WriteFloat f, n\EnemyY
		WriteFloat f, n\EnemyZ
		
		WriteString f, n\texture
		
		WriteFloat f, AnimTime(n\obj)
		
		WriteInt f, n\IsDead
		WriteFloat f, n\PathX
		WriteFloat f, n\PathZ
		WriteInt f, n\HP
		WriteString f, n\Model
		WriteFloat f, n\ModelScaleX#
		WriteFloat f, n\ModelScaleY#
		WriteFloat f, n\ModelScaleZ#
		WriteInt f, n\TextureID
	Next
	
	WriteFloat f, MTFtimer
	For i = 0 To 6
		If MTFrooms[0]<>Null Then 
			WriteString f, MTFrooms[0]\RoomTemplate\Name 
		Else 
			WriteString f,	"a"
		EndIf
		WriteInt f, MTFroomState[i]
	Next
	
	WriteInt f, 632
	
	WriteInt f, room2gw_brokendoor
	WriteFloat f,room2gw_x
	WriteFloat f,room2gw_z
	
	temp = 0
	For r.Rooms = Each Rooms
		temp=temp+1
	Next	
	WriteInt f, temp	
	For r.Rooms = Each Rooms
		WriteInt f, r\RoomTemplate\id
		WriteInt f, r\angle
		WriteFloat f, r\x
		WriteFloat f, r\y
		WriteFloat f, r\z
		
		WriteByte f, r\found
		
		WriteInt f, r\zone
		
		If PlayerRoom = r Then
			WriteByte f, 1
		Else 
			WriteByte f, 0
		EndIf
		
		For i = 0 To 11
			If r\NPC[i]=Null Then
				WriteInt f, 0
			Else
				WriteInt f, r\NPC[i]\ID
			EndIf
		Next
		
		For i=0 To 10
			If r\Levers[i]<>0 Then
				If EntityPitch(r\Levers[i],True) > 0 Then ;p??????ll???
					WriteByte(f,1)
				Else
					WriteByte(f,0)
				EndIf	
			EndIf
		Next
		WriteByte(f,2)
		
		
		If r\grid=Null Then ;this room doesn't have a grid
			WriteByte(f,0)
		Else ;this room has a grid
			WriteByte(f,1)
			For y=0 To gridsz-1
				For x=0 To gridsz-1
					WriteByte(f,r\grid\grid[x+(y*gridsz)])
					WriteByte(f,r\grid\angles[x+(y*gridsz)])
				Next
			Next
		EndIf
		
		If r\fr=Null Then ;this room doesn't have a forest
			WriteByte(f,0)
		Else ;this room has a forestte(f,2)
			For y=0 To gridsize-1
				For x=0 To gridsize-1
					WriteByte(f,r\fr\grid[x+(y*gridsize)])
				Next
			Next
			WriteFloat f,EntityX(r\fr\Forest_Pivot,True)
			WriteFloat f,EntityY(r\fr\Forest_Pivot,True)
			WriteFloat f,EntityZ(r\fr\Forest_Pivot,True)
		EndIf
	Next
	
	WriteInt f, 954
	
	temp = 0
	For do.Doors = Each Doors
		temp = temp+1	
	Next	
	WriteInt f, temp	
	For do.Doors = Each Doors
		WriteFloat f, EntityX(do\frameobj,True)
		WriteFloat f, EntityY(do\frameobj,True)
		WriteFloat f, EntityZ(do\frameobj,True)
		WriteByte f, do\open
		WriteFloat f, do\openstate
		WriteByte f, do\locked
		WriteByte f, do\AutoClose
		
		WriteFloat f, EntityX(do\obj, True)
		WriteFloat f, EntityZ(do\obj, True)
		
		If do\obj2 <> 0 Then
			WriteFloat f, EntityX(do\obj2, True)
			WriteFloat f, EntityZ(do\obj2, True)
		Else
			WriteFloat f, 0.0
			WriteFloat f, 0.0
		EndIf
		
		WriteFloat f, do\timer
		WriteFloat f, do\timerstate
		
		WriteByte f, do\IsElevatorDoor
		WriteByte f, do\MTFClose
	Next
	
	WriteInt f, 1845
	DebugLog 1845
	
	Local d.Decals
	temp = 0
	For d.Decals = Each Decals
		temp = temp+1
	Next	
	WriteInt f, temp
	For d.Decals = Each Decals
		WriteInt f, d\ID
		
		WriteFloat f, EntityX(d\obj,True)
		WriteFloat f, EntityY(d\obj,True)
		WriteFloat f, EntityZ(d\obj,True)
		
		WriteFloat f, EntityPitch(d\obj,True)
		WriteFloat f, EntityYaw(d\obj,True)
		WriteFloat f, EntityRoll(d\obj,True)
		
		WriteByte f, d\blendmode
		WriteInt f, d\fx
		
		WriteFloat f, d\Size
		WriteFloat f, d\Alpha
		WriteFloat f, d\AlphaChange
		WriteFloat f, d\Timer
		WriteFloat f, d\lifetime
	Next
	
	Local e.Events
	temp = 0
	For e.Events = Each Events
		temp=temp+1
	Next	
	WriteInt f, temp
	For e.Events = Each Events
		WriteString f, e\EventName
		WriteFloat f, e\EventState
		WriteFloat f, e\EventState2	
		WriteFloat f, e\EventState3	
		WriteFloat f, EntityX(e\room\obj)
		WriteFloat f, EntityY(e\room\obj)
		WriteFloat f, EntityZ(e\room\obj)
		WriteString f, e\EventStr
	Next
	
	temp = 0
	For it.items = Each Items	
		temp=temp+1
	Next
	WriteInt f, temp
	For it.items = Each Items
		WriteString f, it\itemtemplate\namespec
		WriteString f, it\itemtemplate\tempName
		
		WriteFloat f, EntityX(it\collider, True)
		WriteFloat f, EntityY(it\collider, True)
		WriteFloat f, EntityZ(it\collider, True)
		
		WriteByte f, it\r
		WriteByte f, it\g
		WriteByte f, it\b
		WriteFloat f, it\a
		
		WriteFloat f, it\state2
		
		WriteFloat f, EntityPitch(it\collider)
		WriteFloat f, EntityYaw(it\collider)
		
		WriteFloat f, it\state
		WriteByte f, it\Picked
		
		If SelectedItem = it Then WriteByte f, 1 Else WriteByte f, 0
		Local ItemFound% = False
		For i = 0 To MaxItemAmount - 1
			If Inventory(i) = it Then ItemFound = True : Exit
		Next
		If ItemFound Then WriteByte f, i Else WriteByte f, 66
		
		If it\itemtemplate\isAnim<>0 Then
			WriteFloat f, AnimTime(it\model)
		EndIf
		WriteByte f,it\invSlots
		WriteInt f,it\ID
		If it\itemtemplate\invimg=it\invimg Then WriteByte f,0 Else WriteByte f,1
	Next
	
	temp=0
	For it.items = Each Items
		If it\invSlots>0 Then temp=temp+1
	Next
	
	WriteInt f,temp
	
	For it.items = Each Items
		;OtherInv
		If it\invSlots>0 Then
			WriteInt f,it\ID
			For i=0 To it\invSlots-1
				If it\SecondInv[i] <> Null Then
					WriteInt f, it\SecondInv[i]\ID
				Else
					WriteInt f, -1
				EndIf
			Next
		EndIf
		;OtherInv End
	Next
	
	For itt.itemtemplates = Each ItemTemplates
		WriteByte f, itt\found
	Next
	
	If UsedConsole
		WriteInt f, 100
		DebugLog "Used Console"
	Else
		WriteInt f, 994
	EndIf
	WriteFloat f, CameraFogFar
	WriteFloat f, StoredCameraFogFar
	
	Local I_427.SCP427 = First SCP427
	WriteByte f, I_427\Using
	WriteFloat f, I_427\Timer
	
	WriteByte f, Wearing714
	CloseFile f
	
	If Not MenuOpen Then
		If SelectedDifficulty\saveType = SAVEONSCREENS Then
			PlaySound_Strict(LoadTempSound("SFX\General\Save2.ogg"))
		Else
			PlaySound_Strict(LoadTempSound("SFX\General\Save1.ogg"))
		EndIf
		
		Msg = GetLocalString("Messages", "saved")
		MsgTimer = 70 * 4
		;SetSaveMSG("Game progress saved.")
	EndIf
	
	;CatchErrors("Uncaught SaveGame")
End Function

Function LoadGame(file$)
	Local version$ = ""
	
	;CatchErrors("LoadGame")
	DebugLog "---------------------------------------------------------------------------"
	
	Local I_Cheats.Cheats = First Cheats
	Local I_Loc.Loc = First Loc
	
	DropSpeed=0.0
	
	DebugHUD = False
	
	GameSaved = True
	
	Local x#, y#, z#, i%, temp%, strtemp$, r.Rooms, id%, n.NPCs, do.Doors
	Local f% = ReadFile(file + "save.txt")
	
	strtemp = ReadString(f)
	strtemp = ReadString(f)
	
	PlayTime = ReadInt(f)
	
	x = ReadFloat(f)
	y = ReadFloat(f)
	z = ReadFloat(f)	
	PositionEntity(Collider, x, y+0.05, z)
	ResetEntity(Collider)
	
	x = ReadFloat(f)
	y = ReadFloat(f)
	z = ReadFloat(f)	
	PositionEntity(Head, x, y+0.05, z)
	ResetEntity(Head)
	
	AccessCode = Int(ReadString(f))
	
	x = ReadFloat(f)
	y = ReadFloat(f)
	RotateEntity(Collider, x, y, 0, 0)
	
	strtemp = ReadString(f)
	version = strtemp
	
	If I_Loc\Lang <> ReadString(f) Then RuntimeError("Savegame has different localization, couldn't load")
	
	BlinkTimer = ReadFloat(f)
	BlinkEffect = ReadFloat(f)	
	BlinkEffectTimer = ReadFloat(f)
	
	DeathTimer = ReadInt(f)	
	BlurTimer = ReadInt(f)	
	HealTimer = ReadFloat(f)
	
	Crouch = ReadByte(f)
	
	Stamina = ReadFloat(f)
	StaminaEffect = ReadFloat(f)	
	StaminaEffectTimer = ReadFloat(f)	
	
	EyeStuck = ReadFloat(f)
	EyeIrritation = ReadFloat(f)
	
	Injuries = ReadFloat(f)
	Bloodloss = ReadFloat(f)
	
	PrevInjuries = ReadFloat(f)
	PrevBloodloss = ReadFloat(f)
	
	DeathMSG = ReadString(f)
	
	For i = 0 To 7
		SCP1025state[i]=ReadFloat(f)
	Next
	
	VomitTimer = ReadFloat(f)
	Vomit = ReadByte(f)
	CameraShakeTimer = ReadFloat(f)
	Infect = ReadFloat(f)
	
	Local difficultyIndex = ReadByte(f)
	SelectedDifficulty = difficulties[difficultyIndex]
	If (difficultyIndex = CUSTOM) Then
		SelectedDifficulty\aggressiveNPCs = ReadByte(f)
		SelectedDifficulty\permaDeath = ReadByte(f)
		SelectedDifficulty\saveType	= ReadByte(f)
		SelectedDifficulty\otherFactors = ReadByte(f)
	EndIf
	
	MonitorTimer = ReadFloat(f)
	
	Sanity = ReadFloat(f)
	
	WearingGasMask = ReadByte(f)
	WearingVest = ReadByte(f)	
	WearingHazmat = ReadByte(f)
	
	WearingNightVision = ReadByte(f)
	Wearing1499 = ReadByte(f)
	NTF_1499PrevX# = ReadFloat(f)
	NTF_1499PrevY# = ReadFloat(f)
	NTF_1499PrevZ# = ReadFloat(f)
	NTF_1499X# = ReadFloat(f)
	NTF_1499Y# = ReadFloat(f)
	NTF_1499Z# = ReadFloat(f)
	Local r1499_x# = ReadFloat(f)
	Local r1499_z# = ReadFloat(f)
	
	I_Cheats\SuperMan = ReadByte(f)
	I_Cheats\SuperManTimer = ReadFloat(f)
	LightsOn = ReadByte(f)
	
	RandomSeed = ReadString(f)
	
	SecondaryLightOn = ReadFloat(f)
	PrevSecondaryLightOn = ReadFloat(f)
	RemoteDoorOn = ReadByte(f)
	SoundTransmission = ReadByte(f)	
	Contained106 = ReadByte(f)	
	
	For i = 0 To MAXACHIEVEMENTS-1
		Achievements[i]=ReadByte(f)
	Next
	RefinedItems = ReadInt(f)
	
	For lvl = 0 To 2
		For x = 0 To MapWidth 
			For y = 0 To MapHeight
				MapTemp(x, y) = ReadInt(f)
				MapFound(x, y) = ReadByte(f)
			Next
		Next
	Next
	
	If ReadInt(f) <> 113 Then RuntimeError("Couldn't load the game, save file corrupted (error 2.5)")
	
	temp = ReadInt(f)
	For i = 1 To temp
		Local NPCtype% = ReadByte(f)
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		
		n.NPCs = CreateNPC(NPCtype, x, y, z)
		Select NPCtype
			Case NPCtype173
				Curr173 = n
			Case NPCtypeOldMan
				Curr106 = n
			Case NPCtype096
				Curr096 = n
			Case NPCtype5131
				Curr5131 = n
		End Select
		
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		RotateEntity(n\Collider, x, y, z)
		
		n\State = ReadFloat(f)
		n\State2 = ReadFloat(f)	
		n\State3 = ReadFloat(f)			
		n\PrevState = ReadInt(f)
		
		n\Idle = ReadByte(f)
		n\LastDist = ReadFloat(f)
		n\LastSeen = ReadInt(f)
		
		n\CurrSpeed = ReadInt(f)
		n\Angle = ReadFloat(f)
		n\Reload = ReadFloat(f)
		
		ForceSetNPCID(n, ReadInt(f))
		n\TargetID = ReadInt(f)
		
		DebugLog("Loading NPC " +n\NVName+ " (ID "+n\ID+")")
		
		n\EnemyX = ReadFloat(f)
		n\EnemyY = ReadFloat(f)
		n\EnemyZ = ReadFloat(f)
		
		n\texture = ReadString(f)
		If n\texture <> "" Then
			tex = LoadTexture_Strict (n\texture)
			EntityTexture n\obj, tex
		EndIf
		
		Local frame# = ReadFloat(f)
		Select NPCtype
			Case NPCtypeOldMan, NPCtypeD, NPCtype096, NPCtypeMTF, NPCtypeGuard, NPCtype049, NPCtypeZombie, NPCtypeClerk
				SetAnimTime(n\obj, frame)
		End Select
		
		n\Frame = frame
		
		n\IsDead = ReadInt(f)
		n\PathX = ReadFloat(f)
		n\PathZ = ReadFloat(f)
		n\HP = ReadInt(f)
		n\Model = ReadString(f)
		n\ModelScaleX# = ReadFloat(f)
		n\ModelScaleY# = ReadFloat(f)
		n\ModelScaleZ# = ReadFloat(f)
		If n\Model <> ""
			FreeEntity n\obj
			n\obj = LoadAnimMesh_Strict(n\Model)
			ScaleEntity n\obj,n\ModelScaleX,n\ModelScaleY,n\ModelScaleZ
			SetAnimTime n\obj,frame
		EndIf
		n\TextureID = ReadInt(f)
		If n\TextureID > 0
			ChangeNPCTextureID(n.NPCs,n\TextureID-1)
			SetAnimTime(n\obj,frame)
		EndIf
	Next
	
	For n.NPCs = Each NPCs
		If n\TargetID <> 0 Then
			For n2.npcs = Each NPCs
				If n2<>n Then
					If n2\id = n\TargetID Then n\Target = n2
				EndIf
			Next
		EndIf
	Next
	
	MTFtimer = ReadFloat(f)
	For i = 0 To 6
		strtemp =  ReadString(f)
		If strtemp <> "a" Then
			For r.Rooms = Each Rooms
				If r\RoomTemplate\Name = strtemp Then
					MTFrooms[i]=r
				EndIf
			Next
		EndIf
		MTFroomState[i]=ReadInt(f)
	Next
	
	If ReadInt(f) <> 632 Then RuntimeError("Couldn't load the game, save file corrupted (error 1)")
	
	room2gw_brokendoor = ReadInt(f)
	room2gw_x = ReadFloat(f)
	room2gw_z = ReadFloat(f)
	
	temp = ReadInt(f)
	For i = 1 To temp
		Local roomtemplateID% = ReadInt(f)
		Local angle% = ReadInt(f)
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		
		found = ReadByte(f)
		
		level = ReadInt(f)
		
		temp2 = ReadByte(f)		
		
		angle=WrapAngle(angle)
		
		For rt.roomtemplates = Each RoomTemplates
			If rt\id = roomtemplateID Then
				r.Rooms = CreateRoom(level, rt\shape, x, y, z, rt\name)
				TurnEntity(r\obj, 0, angle, 0)
				r\angle = angle
				r\found = found
				Exit
			EndIf
		Next
		
		If temp2 = 1 Then PlayerRoom = r.Rooms
		
		For x = 0 To 11
			id = ReadInt(f)
			If id > 0 Then
				For n.NPCs = Each NPCs
					If n\ID = id Then r\NPC[x]=n : Exit
				Next
			EndIf
		Next
		
		For x=0 To 11
			id = ReadByte(f)
			If id=2 Then
				Exit
			Else If id=1 Then
				RotateEntity(r\Levers[x], 78, EntityYaw(r\Levers[x]), 0)
			Else
				RotateEntity(r\Levers[x], -78, EntityYaw(r\Levers[x]), 0)
			EndIf
		Next
		
		If ReadByte(f)=1 Then ;this room has a grid
			If r\grid<>Null Then ;remove the old grid content
				For x=0 To gridsz-1
					For y=0 To gridsz-1
						If r\grid\Entities[x+(y*gridsz)]<>0 Then
							FreeEntity r\grid\Entities[x+(y*gridsz)]
							r\grid\Entities[x+(y*gridsz)]=0
						EndIf
						If r\grid\waypoints[x+(y*gridsz)]<>Null Then
							RemoveWaypoint(r\grid\waypoints[x+(y*gridsz)])
							r\grid\waypoints[x+(y*gridsz)]=Null
						EndIf
					Next
				Next
				For x=0 To 5
					If r\grid\Meshes[x]<>0 Then
						FreeEntity r\grid\Meshes[x]
						r\grid\Meshes[x]=0
					EndIf
				Next
				Delete r\grid
			EndIf
			r\grid=New Grids
			For y=0 To gridsz-1
				For x=0 To gridsz-1
					r\grid\grid[x+(y*gridsz)]=ReadByte(f)
					r\grid\angles[x+(y*gridsz)]=ReadByte(f)
					;get only the necessary data, make the event handle the meshes and waypoints separately
				Next
			Next
		EndIf
		
		Local hasForest = ReadByte(f)
		If hasForest>0 Then ;this room has a forest
			If r\fr<>Null Then ;remove the old forest
				DestroyForest(r\fr)
			Else
				r\fr=New Forest
			EndIf
			For y=0 To gridsize-1
				Local sssss$ = ""
				For x=0 To gridsize-1
					r\fr\grid[x+(y*gridsize)]=ReadByte(f)
					sssss=sssss+Str(r\fr\grid[x+(y*gridsize)])
				Next
				DebugLog sssss
			Next
			lx# = ReadFloat(f)
			ly# = ReadFloat(f)
			lz# = ReadFloat(f)
			
			PlaceForest(r\fr,lx,ly,lz,r)
		ElseIf r\fr<>Null Then ;remove the old forest
			DestroyForest(r\fr)
			Delete r\fr
		EndIf
		
	Next
	
	For r.Rooms = Each Rooms
		If r\x = r1499_x# And r\z = r1499_z#
			NTF_1499PrevRoom = r
			Exit
		EndIf
	Next
	
	If ReadInt(f) <> 954 Then RuntimeError("Couldn't load the game, save file may be corrupted (error 2)")
	
	Local spacing# = 8.0
	Local shouldSpawnDoor%
	For zone% = DO_DA_ZONE To DO_DA_ZONE
		For y = MapHeight To 0 Step -1
			For x = MapWidth To 0 Step -1
				If MapTemp(x,y) > 0 Then
					
					For r.Rooms = Each Rooms
						r\angle = WrapAngle(r\angle)
						If Int(r\x/8.0)=x And Int(r\z/8.0)=y Then
							shouldSpawnDoor = False
							Select r\RoomTemplate\Shape
								Case ROOM1
									If r\angle=90
										shouldSpawnDoor = True
									EndIf
								Case ROOM2
									If r\angle=90 Lor r\angle=270
										shouldSpawnDoor = True
									EndIf
								Case ROOM2C
									If r\angle=0 Lor r\angle=90
										shouldSpawnDoor = True
									EndIf
								Case ROOM3
									If r\angle=0 Lor r\angle=180 Lor r\angle=90
										shouldSpawnDoor = True
									EndIf
								Default
									shouldSpawnDoor = True
							End Select
							If shouldSpawnDoor
								If (x+1)<(MapWidth+1)
									If MapTemp(x + 1, y) > 0 Then
										do.Doors = CreateDoor(r\zone, Float(x) * spacing + spacing / 2.0, 0, Float(y) * spacing, 90, r, Max(Rand(-3, 1), 0), (zone Mod 2) * 2)
										r\AdjDoor[0] = do
									EndIf
								EndIf
							EndIf
							
							shouldSpawnDoor = False
							Select r\RoomTemplate\Shape
								Case ROOM1
									If r\angle=180
										shouldSpawnDoor = True
									EndIf
								Case ROOM2
									If r\angle=0 Lor r\angle=180
										shouldSpawnDoor = True
									EndIf
								Case ROOM2C
									If r\angle=180 Lor r\angle=90
										shouldSpawnDoor = True
									EndIf
								Case ROOM3
									If r\angle=180 Lor r\angle=90 Lor r\angle=270
										shouldSpawnDoor = True
									EndIf
								Default
									shouldSpawnDoor = True
							End Select
							If shouldSpawnDoor
								If (y+1)<(MapHeight+1)
									If MapTemp(x, y + 1) > 0 Then
										do.Doors = CreateDoor(r\zone, Float(x) * spacing, 0, Float(y) * spacing + spacing / 2.0, 0, r, Max(Rand(-3, 1), 0), (zone Mod 2) * 2)
										r\AdjDoor[3] = do
									EndIf
								EndIf
							EndIf
							
							Exit
						EndIf
					Next
					
				EndIf
				
			Next
		Next
	Next
	
	temp = ReadInt (f)
	
	For i = 1 To temp
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		
		Local open% = ReadByte(f)
		Local openstate# = ReadFloat(f)
		Local locked% = ReadByte(f)
		Local autoclose% = ReadByte(f)
		
		Local objX# = ReadFloat(f)
		Local objZ# = ReadFloat(f)
		
		Local obj2X# = ReadFloat(f)
		Local obj2Z# = ReadFloat(f)
		
		Local timer% = ReadFloat(f)
		Local timerstate# = ReadFloat(f)
		
		Local IsElevDoor = ReadByte(f)
		Local MTFClose = ReadByte(f)
		
		For do.Doors = Each Doors
			If EntityX(do\frameobj,True) = x And EntityY(do\frameobj,True) = y And EntityZ(do\frameobj,True) = z Then
				do\open = open
				do\openstate = openstate
				do\locked = locked
				do\AutoClose = autoclose
				do\timer = timer
				do\timerstate = timerstate
				do\IsElevatorDoor = IsElevDoor
				do\MTFClose = MTFClose
				
				PositionEntity(do\obj, objX, y, objZ, True)
				If do\obj2 <> 0 Then PositionEntity(do\obj2, obj2X, y, obj2Z, True)
				Exit
			EndIf
		Next		
	Next
	
	InitWayPoints()
	
	If ReadInt(f) <> 1845 Then RuntimeError("Couldn't load the game, save file corrupted (error 3)")
	
	Local d.Decals
	For d.Decals = Each Decals
		FreeEntity d\obj
		Delete d
	Next
	
	temp = ReadInt(f)
	For i = 1 To temp
		id% = ReadInt(f)
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		Local pitch# = ReadFloat(f)
		Local yaw# = ReadFloat(f)
		Local roll# = ReadFloat(f)
		d.Decals = CreateDecal(id, x, y, z, pitch, yaw, roll)
		d\blendmode = ReadByte (f)
		d\fx = ReadInt(f)
		
		d\Size = ReadFloat(f)
		d\Alpha = ReadFloat(f)
		d\AlphaChange = ReadFloat(f)
		d\Timer = ReadFloat(f)
		d\lifetime = ReadFloat(f)
		
		ScaleSprite(d\obj, d\Size, d\Size)
		EntityBlend d\obj, d\blendmode
		EntityFX d\obj, d\fx
		
		DebugLog "Created Decal @"+x+","+y+","+z
	Next
	UpdateDecals()
	
	temp = ReadInt(f)
	For i = 1 To temp
		Local e.Events = New Events
		e\EventName = ReadString(f)
		
		e\EventState =ReadFloat(f)
		e\EventState2 =ReadFloat(f)		
		e\EventState3 =ReadFloat(f)
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		For  r.Rooms = Each Rooms
			If EntityX(r\obj) = x And EntityY(r\obj) = y And EntityZ(r\obj) = z Then
				e\room = r
				Exit
			EndIf
		Next
		e\EventStr = ReadString(f)
	Next
	
	For e.Events = Each Events
		;Reset for the monitor loading and stuff for room2sl
		If e\EventName = "room2sl"
			e\EventState = 0.0
			e\EventStr = ""
			DebugLog "Reset Eventstate in "+e\EventName
		;Reset dimension1499
		ElseIf e\EventName = "dimension1499"
			If e\EventState > 0.0
				e\EventState = 0.0
				e\EventStr = ""
				HideChunks()
				DeleteChunks()
				For n.NPCs = Each NPCs
					If n\NPCtype = NPCtype1499
						If n\InFacility = 0
							RemoveNPC(n)
						EndIf
					EndIf
				Next
				DebugLog "Reset Eventstate in "+e\EventName
			EndIf
		;Reset the forest event to make it loading properly
		ElseIf e\EventName = "room860"
			e\EventStr = ""
		ElseIf e\EventName = "room205"
			e\EventStr = ""
		ElseIf e\EventName = "room106"
			If e\EventState2 = False Then
				PositionEntity (e\room\Objects[6],EntityX(e\room\Objects[6],True),-1280.0*RoomScale,EntityZ(e\room\Objects[6],True),True)
			EndIf
		EndIf
	Next
	
	Local it.Items
	For it.Items = Each Items
		RemoveItem(it)
	Next
	
	temp = ReadInt(f)
	For i = 1 To temp
		Local ittName$ = ReadString(f)
		Local tempName$ = ReadString(f)
		
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		
		red = ReadByte(f)
		green = ReadByte(f)
		blue = ReadByte(f)		
		a = ReadFloat(f)
		
		it.Items = CreateItem(tempName, x, y, z, ittName, red, green, blue, a)
		
		it\state2 = ReadFloat(f)
		
		EntityType it\collider, HIT_ITEM
		
		x = ReadFloat(f)
		y = ReadFloat(f)
		RotateEntity(it\collider, x, y, 0)
		
		it\state = ReadFloat(f)
		it\Picked = ReadByte(f)
		If it\Picked > 0 Then HideEntity(it\collider)
		
		nt = ReadByte(f)
		If nt = True Then SelectedItem = it
		
		nt = ReadByte(f)
		If nt < 66
			Inventory(nt) = it
			ItemAmount = ItemAmount + 1
		EndIf
		
		For itt.ItemTemplates = Each ItemTemplates
			If (itt\tempname = tempName) And (itt\namespec = ittName) Then
				If itt\isAnim<>0 Then SetAnimTime it\model,ReadFloat(f) : Exit
			EndIf
		Next
		it\invSlots = ReadByte(f)
		it\ID = ReadInt(f)
		
		If it\ID>LastItemID Then LastItemID=it\ID
		
		If ReadByte(f)=0 Then
			it\invimg=it\itemtemplate\invimg
		Else
			it\invimg=it\itemtemplate\invimg2
		EndIf
	Next	
	
	Local o_i%
	
	temp = ReadInt(f)
	For i=1 To temp
		;OtherInv
		o_i=ReadInt(f)
		
		For ij.Items = Each Items
			If ij\ID=o_i Then it.Items=ij : Exit
		Next
		For j%=0 To it\invSlots-1
			o_i=ReadInt(f)
			DebugLog "secondinv "+o_i
			If o_i<>-1 Then
				For ij.Items=Each Items
					If ij\ID=o_i Then
						it\SecondInv[j]=ij
						Exit
					EndIf
				Next
			EndIf
		Next
		;OtherInv End
	Next
	
	For itt.ItemTemplates = Each ItemTemplates
		itt\found = ReadByte(f)
	Next
	
	For do.Doors = Each Doors
		If do\room <> Null Then
			dist# = 20.0
			Local closestroom.Rooms
			For r.Rooms = Each Rooms
				dist2# = EntityDistance(r\obj, do\obj)
				If dist2 < dist Then
					dist = dist2
					closestroom = r.Rooms
				EndIf
			Next
			do\room = closestroom
		EndIf
	Next
	
	;If ReadInt(f) <> 994 Then RuntimeError("Couldn't load the game, save file corrupted (error 4)")
	
	If ReadInt(f)<>994
		UsedConsole = True
		DebugLog "Used Console"
	EndIf
	
	CameraFogFar = ReadFloat(f)
	StoredCameraFogFar = ReadFloat(f)
	If CameraFogFar = 0 Then
		CameraFogFar = 6
	EndIf
	
	Local I_427.SCP427 = First SCP427
	I_427\Using = ReadByte(f)
	I_427\Timer = ReadFloat(f)
	
	Wearing714 = ReadByte(f)
	
	CloseFile f
	
	For r.Rooms = Each Rooms
		r\Adjacent[0]=Null
		r\Adjacent[1]=Null
		r\Adjacent[2]=Null
		r\Adjacent[3]=Null
		For r2.Rooms = Each Rooms
			If r<>r2 Then
				If r\zone=r2\zone Then
					If r2\z=r\z Then
						If (r2\x)=(r\x+8.0) Then
							r\Adjacent[0]=r2
							If r\AdjDoor[0] = Null Then r\AdjDoor[0] = r2\AdjDoor[2]
						ElseIf (r2\x)=(r\x-8.0)
							r\Adjacent[2]=r2
							If r\AdjDoor[2] = Null Then r\AdjDoor[2] = r2\AdjDoor[0]
						EndIf
					ElseIf r2\x=r\x Then
						If (r2\z)=(r\z-8.0) Then
							r\Adjacent[1]=r2
							If r\AdjDoor[1] = Null Then r\AdjDoor[1] = r2\AdjDoor[3]
						ElseIf (r2\z)=(r\z+8.0)
							r\Adjacent[3]=r2
							If r\AdjDoor[3] = Null Then r\AdjDoor[3] = r2\AdjDoor[1]
						EndIf
					EndIf
				EndIf
			EndIf
			If (r\Adjacent[0]<>Null) And (r\Adjacent[1]<>Null) And (r\Adjacent[2]<>Null) And (r\Adjacent[3]<>Null) Then Exit
		Next
		
		For do.Doors = Each Doors
			If (do\KeyCard = 0) And (do\Code="")
				If EntityZ(do\frameobj,True)=r\z Then
					If EntityX(do\frameobj,True)=r\x+4.0 Then
						r\AdjDoor[0] = do
					ElseIf EntityX(do\frameobj,True)=r\x-4.0 Then
						r\AdjDoor[2] = do
					EndIf
				ElseIf EntityX(do\frameobj,True)=r\x Then
					If EntityZ(do\frameobj,True)=r\z+4.0 Then
						r\AdjDoor[3] = do
					ElseIf EntityZ(do\frameobj,True)=r\z-4.0 Then
						r\AdjDoor[1] = do
					EndIf
				EndIf
			EndIf
		Next
	Next
	
	If PlayerRoom\RoomTemplate\Name = "dimension1499"
		BlinkTimer = -1
		ShouldEntitiesFall = False
		PlayerRoom = NTF_1499PrevRoom
		UpdateDoors()
		UpdateRooms()
		For it.Items = Each Items
			it\disttimer = 0
		Next
	EndIf
	
	If Collider <> 0 Then
		If PlayerRoom<>Null Then
			ShowEntity PlayerRoom\obj
		EndIf
		ShowEntity Collider
		TeleportEntity(Collider,EntityX(Collider),EntityY(Collider)+0.5,EntityZ(Collider),0.3,True)
		If PlayerRoom<>Null Then
			HideEntity PlayerRoom\obj
		EndIf
	EndIf
	
	UpdateDoorsTimer = 0
	
	;CatchErrors("Uncaught LoadGame")
End Function

Function LoadGameQuick(file$)
	Local version$ = ""
	
	;CatchErrors("LoadGameQuick")
	DebugLog "---------------------------------------------------------------------------"
	
	Local I_Cheats.Cheats = First Cheats
	Local I_Loc.Loc = First Loc
	
	DebugHUD = False
	GameSaved = True
	NoTarget = False
	InfiniteStamina = False
	IsZombie% = False
	DeafPlayer% = False
	DeafTimer# = 0.0
	UnableToMove% = False
	Msg = ""
	SelectedEnding = ""
	
	PositionEntity Collider,0,1000.0,0,True
	ResetEntity Collider
	
	Local x#, y#, z#, i%, temp%, strtemp$, id%
	Local player_x#,player_y#,player_z#, r.Rooms, n.NPCs, do.Doors
	Local f% = ReadFile(file + "save.txt")
	
	strtemp = ReadString(f)
	strtemp = ReadString(f)
	
	DropSpeed = -0.1
	HeadDropSpeed = 0.0
	Shake = 0
	CurrSpeed = 0
	
	HeartBeatVolume = 0
	
	CameraShake = 0
	Shake = 0
	LightFlash = 0
	BlurTimer = 0
	
	KillTimer = 0
	FallTimer = 0
	MenuOpen = False
	
	ClearCheats(I_Cheats)
	
	PlayTime = ReadInt(f)
	
	;HideEntity Head
	HideEntity Collider
	
	x = ReadFloat(f)
	y = ReadFloat(f)
	z = ReadFloat(f)	
	PositionEntity(Collider, x, y+0.05, z)
	;ResetEntity(Collider)
	
	ShowEntity Collider
	
	x = ReadFloat(f)
	y = ReadFloat(f)
	z = ReadFloat(f)	
	PositionEntity(Head, x, y+0.05, z)
	ResetEntity(Head)
	
	AccessCode = Int(ReadString(f))
	
	x = ReadFloat(f)
	y = ReadFloat(f)
	RotateEntity(Collider, x, y, 0, 0)
	
	strtemp = ReadString(f)
	version = strtemp
	
	If I_Loc\Lang <> ReadString(f) Then RuntimeError("Savegame has different localization, couldn't load")
	
	BlinkTimer = ReadFloat(f)
	BlinkEffect = ReadFloat(f)	
	BlinkEffectTimer = ReadFloat(f)	
	
	DeathTimer = ReadInt(f)	
	BlurTimer = ReadInt(f)	
	HealTimer = ReadFloat(f)
	
	Crouch = ReadByte(f)
	
	Stamina = ReadFloat(f)
	StaminaEffect = ReadFloat(f)	
	StaminaEffectTimer = ReadFloat(f)	
	
	EyeStuck	= ReadFloat(f)
	EyeIrritation= ReadFloat(f)
	
	Injuries = ReadFloat(f)
	Bloodloss = ReadFloat(f)
	
	PrevInjuries = ReadFloat(f)
	PrevBloodloss = ReadFloat(f)
	
	DeathMSG = ReadString(f)
	
	For i = 0 To 7
		SCP1025state[i]=ReadFloat(f)
	Next
	
	VomitTimer = ReadFloat(f)
	Vomit = ReadByte(f)
	CameraShakeTimer = ReadFloat(f)
	Infect = ReadFloat(f)
	
	Local difficultyIndex = ReadByte(f)
	SelectedDifficulty = difficulties[difficultyIndex]
	If (difficultyIndex = CUSTOM) Then
		SelectedDifficulty\aggressiveNPCs = ReadByte(f)
		SelectedDifficulty\permaDeath = ReadByte(f)
		SelectedDifficulty\saveType	= ReadByte(f)
		SelectedDifficulty\otherFactors = ReadByte(f)
	EndIf
	
	MonitorTimer = ReadFloat(f)
	
	Sanity = ReadFloat(f)
	
	WearingGasMask = ReadByte(f)
	WearingVest = ReadByte(f)	
	WearingHazmat = ReadByte(f)
	
	WearingNightVision = ReadByte(f)
	Wearing1499 = ReadByte(f)
	NTF_1499PrevX# = ReadFloat(f)
	NTF_1499PrevY# = ReadFloat(f)
	NTF_1499PrevZ# = ReadFloat(f)
	NTF_1499X# = ReadFloat(f)
	NTF_1499Y# = ReadFloat(f)
	NTF_1499Z# = ReadFloat(f)
	Local r1499_x# = ReadFloat(f)
	Local r1499_z# = ReadFloat(f)
	
	I_Cheats\SuperMan = ReadByte(f)
	I_Cheats\SuperManTimer = ReadFloat(f)
	LightsOn = ReadByte(f)
	
	RandomSeed = ReadString(f)
	
	SecondaryLightOn = ReadFloat(f)
	PrevSecondaryLightOn = ReadFloat(f)
	RemoteDoorOn = ReadByte(f)
	SoundTransmission = ReadByte(f)	
	Contained106 = ReadByte(f)	
	
	For i = 0 To MAXACHIEVEMENTS-1
		Achievements[i]=ReadByte(f)
	Next
	RefinedItems = ReadInt(f)
	
	For lvl = 0 To 2
		For x = 0 To MapWidth
			For y = 0 To MapHeight
				MapTemp(x, y) = ReadInt(f)
				MapFound(x, y) = ReadByte(f)
			Next
		Next
	Next
	
	If ReadInt(f) <> 113 Then RuntimeError("Couldn't load the game, save file corrupted (error 2.5)")
	
	For n.NPCs = Each NPCs
		RemoveNPC(n)
	Next
	
	temp = ReadInt(f)
	For i = 1 To temp
		Local NPCtype% = ReadByte(f)
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		
		n.NPCs = CreateNPC(NPCtype, x, y, z)
		Select NPCtype
			Case NPCtype173
				Curr173 = n
			Case NPCtypeOldMan
				Curr106 = n
			Case NPCtype096
				Curr096 = n
			Case NPCtype5131
				Curr5131 = n
		End Select
		
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		RotateEntity(n\Collider, x, y, z)
		
		n\State = ReadFloat(f)
		n\State2 = ReadFloat(f)	
		n\State3 = ReadFloat(f)			
		n\PrevState = ReadInt(f)
		
		n\Idle = ReadByte(f)
		n\LastDist = ReadFloat(f)
		n\LastSeen = ReadInt(f)
		
		n\CurrSpeed = ReadInt(f)
		n\Angle = ReadFloat(f)
		n\Reload = ReadFloat(f)
		
		ForceSetNPCID(n, ReadInt(f))
		n\TargetID = ReadInt(f)
		
		n\EnemyX = ReadFloat(f)
		n\EnemyY = ReadFloat(f)
		n\EnemyZ = ReadFloat(f)
		
		n\texture = ReadString(f)
		If n\texture <> "" Then
			tex = LoadTexture_Strict (n\texture)
			EntityTexture n\obj, tex
		EndIf
		
		Local frame# = ReadFloat(f)
		Select NPCtype
			Case NPCtypeOldMan, NPCtypeD, NPCtype096, NPCtypeMTF, NPCtypeGuard, NPCtype049, NPCtypeZombie, NPCtypeClerk
				SetAnimTime(n\obj, frame)
		End Select		
		
		n\Frame = frame
		
		n\IsDead = ReadInt(f)
		n\PathX = ReadFloat(f)
		n\PathZ = ReadFloat(f)
		n\HP = ReadInt(f)
		n\Model = ReadString(f)
		n\ModelScaleX# = ReadFloat(f)
		n\ModelScaleY# = ReadFloat(f)
		n\ModelScaleZ# = ReadFloat(f)
		If n\Model <> ""
			FreeEntity n\obj
			n\obj = LoadAnimMesh_Strict(n\Model)
			ScaleEntity n\obj,n\ModelScaleX,n\ModelScaleY,n\ModelScaleZ
			SetAnimTime n\obj,frame
		EndIf
		n\TextureID = ReadInt(f)
		If n\TextureID > 0
			ChangeNPCTextureID(n.NPCs,n\TextureID-1)
			SetAnimTime(n\obj,frame)
		EndIf
	Next
	
	For n.NPCs = Each NPCs
		If n\TargetID <> 0 Then
			For n2.npcs = Each NPCs
				If n2<>n Then
					If n2\id = n\TargetID Then n\Target = n2
				EndIf
			Next
		EndIf
	Next
	
	MTFtimer = ReadFloat(f)
	For i = 0 To 6
		strtemp =  ReadString(f)
		If strtemp <> "a" Then
			For r.Rooms = Each Rooms
				If r\RoomTemplate\Name = strtemp Then
					MTFrooms[i]=r
				EndIf
			Next
		EndIf
		MTFroomState[i]=ReadInt(f)
	Next
	
	If ReadInt(f) <> 632 Then RuntimeError("Couldn't load the game, save file corrupted (error 1)")
	
	room2gw_brokendoor = ReadInt(f)
	room2gw_x = ReadFloat(f)
	room2gw_z = ReadFloat(f)
	
	temp = ReadInt(f)
	For i = 1 To temp
		Local roomtemplateID% = ReadInt(f)
		Local angle% = ReadInt(f)
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		
		found = ReadByte(f)
		
		level = ReadInt(f)
		
		temp2 = ReadByte(f)	
		
		If angle >= 360
			angle = angle-360
		EndIf
		
		For r.Rooms = Each Rooms
			If r\x = x And r\z = z Then
				Exit
			EndIf
		Next
		
		For x = 0 To 11
			id = ReadInt(f)
			If id > 0 Then
				For n.NPCs = Each NPCs
					If n\ID = id Then r\NPC[x]=n : Exit
				Next
			EndIf
		Next
		
		For x=0 To 11
			id = ReadByte(f)
			If id=2 Then
				Exit
			Else If id=1 Then
				RotateEntity(r\Levers[x], 78, EntityYaw(r\Levers[x]), 0)
			Else
				RotateEntity(r\Levers[x], -78, EntityYaw(r\Levers[x]), 0)
			EndIf
		Next
		
		If ReadByte(f)=1 Then ;this room has a grid
			For y=0 To gridsz-1
				For x=0 To gridsz-1
					ReadByte(f) : ReadByte(f)
				Next
			Next
		Else ;this grid doesn't exist in the save
			If r\grid<>Null Then
				For x=0 To gridsz-1
					For y=0 To gridsz-1
						If r\grid\Entities[x+(y*gridsz)]<>0 Then
							FreeEntity r\grid\Entities[x+(y*gridsz)]
							r\grid\Entities[x+(y*gridsz)]=0
						EndIf
						If r\grid\waypoints[x+(y*gridsz)]<>Null Then
							RemoveWaypoint(r\grid\waypoints[x+(y*gridsz)])
							r\grid\waypoints[x+(y*gridsz)]=Null
						EndIf
					Next
				Next
				For x=0 To 5
					If r\grid\Meshes[x]<>0 Then
						FreeEntity r\grid\Meshes[x]
						r\grid\Meshes[x]=0
					EndIf
				Next
				Delete r\grid
				r\grid=Null
			EndIf
		EndIf
		
		If ReadByte(f)>0 Then ;this room has a forest
			For y=0 To gridsize-1
				For x=0 To gridsize-1
					ReadByte(f)
				Next
			Next
			lx# = ReadFloat(f)
			ly# = ReadFloat(f)
			lz# = ReadFloat(f)
		ElseIf r\fr<>Null Then ;remove the old forest
			DestroyForest(r\fr)
			Delete r\fr
		EndIf
		
		If temp2 = 1 Then PlayerRoom = r.Rooms
	Next
	
	For r.Rooms = Each Rooms
		If r\x = r1499_x# And r\z = r1499_z#
			NTF_1499PrevRoom = r
			Exit
		EndIf
	Next
	
	;InitWayPoints()
	
	If ReadInt(f) <> 954 Then RuntimeError("Couldn't load the game, save file may be corrupted (error 2)")
	
	temp = ReadInt (f)
	
	For i = 1 To temp
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		
		Local open% = ReadByte(f)
		Local openstate# = ReadFloat(f)
		Local locked% = ReadByte(f)
		Local autoclose% = ReadByte(f)
		
		Local objX# = ReadFloat(f)
		Local objZ# = ReadFloat(f)
		
		Local obj2X# = ReadFloat(f)
		Local obj2Z# = ReadFloat(f)
		
		Local timer% = ReadFloat(f)
		Local timerstate# = ReadFloat(f)
		
		Local IsElevDoor = ReadByte(f)
		Local MTFClose = ReadByte(f)
		
		For do.Doors = Each Doors
			If EntityX(do\frameobj,True) = x Then 
				If EntityZ(do\frameobj,True) = z Then	
					If EntityY(do\frameobj,True) = y 
						do\open = open
						do\openstate = openstate
						do\locked = locked
						do\AutoClose = autoclose
						do\timer = timer
						do\timerstate = timerstate
						do\IsElevatorDoor = IsElevDoor
						do\MTFClose = MTFClose
						
						PositionEntity(do\obj, objX, EntityY(do\obj), objZ, True)
						If do\obj2 <> 0 Then PositionEntity(do\obj2, obj2X, EntityY(do\obj2), obj2Z, True)
						
						Exit
					EndIf
				EndIf
			EndIf
		Next		
	Next
	
	If ReadInt(f) <> 1845 Then RuntimeError("Couldn't load the game, save file corrupted (error 3)")
	
	Local d.Decals
	For d.Decals = Each Decals
		FreeEntity d\obj
		Delete d
	Next
	
	temp = ReadInt(f)
	For i = 1 To temp
		id% = ReadInt(f)
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		Local pitch# = ReadFloat(f)
		Local yaw# = ReadFloat(f)
		Local roll# = ReadFloat(f)
		d.Decals = CreateDecal(id, x, y, z, pitch, yaw, roll)
		d\blendmode = ReadByte (f)
		d\fx = ReadInt(f)
		
		d\Size = ReadFloat(f)
		d\Alpha = ReadFloat(f)
		d\AlphaChange = ReadFloat(f)
		d\Timer = ReadFloat(f)
		d\lifetime = ReadFloat(f)
		
		ScaleSprite(d\obj, d\Size, d\Size)
		EntityBlend d\obj, d\blendmode
		EntityFX d\obj, d\fx
		
		DebugLog "Created Decal @"+x+","+y+","+z
	Next
	UpdateDecals()
	
	Local e.Events
	For e.Events = Each Events
		If e\Sound <> 0 Then FreeSound_Strict e\Sound
		Delete e
	Next
	
	temp = ReadInt(f)
	For i = 1 To temp
		e.Events = New Events
		e\EventName = ReadString(f)
		
		e\EventState = ReadFloat(f)
		e\EventState2 = ReadFloat(f)
		e\EventState3 = ReadFloat(f)		
		x = ReadFloat(f)
		y = ReadFloaT(f)
		z = ReadFloat(f)
		For r.Rooms = Each Rooms
			If EntityX(r\obj) = x And EntityY(r\obj) = y And EntityZ(r\obj) = z Then
				;If e\EventName = "room2servers" Then Stop
				e\room = r
				Exit
			EndIf
		Next	
		e\EventStr = ReadString(f)
		If e\EventName = "alarm"
			;A hacky fix for the case that the intro objects aren't loaded when they should
			;Altough I'm too lazy to add those objects there because at the time where you can save, those objects are already in the ground anyway - ENDSHN
			If e\room\Objects[0]=0
				e\room\Objects[0]=CreatePivot()
				e\room\Objects[1]=CreatePivot()
			EndIf
		ElseIf e\EventName = "room860" Then
			If e\EventState = 1.0 Then
				ShowEntity e\room\fr\Forest_Pivot
			EndIf
		EndIf
	Next
	
	Local it.Items
	For it.Items = Each Items
		RemoveItem(it)
	Next
	
	temp = ReadInt(f)
	For i = 1 To temp
		Local ittName$ = ReadString(f)
		Local tempName$ = ReadString(f)
		
		x = ReadFloat(f)
		y = ReadFloat(f)
		z = ReadFloat(f)
		
		red = ReadByte(f)
		green = ReadByte(f)
		blue = ReadByte(f)		
		a = ReadFloat(f)
		
		it.Items = CreateItem(tempName, x, y, z, ittName, red, green, blue, a)
		it\state2 = ReadFloat(f)
		
		EntityType it\collider, HIT_ITEM
		
		x = ReadFloat(f)
		y = ReadFloat(f)
		RotateEntity(it\collider, x, y, 0)
		
		it\state = ReadFloat(f)
		it\Picked = ReadByte(f)
		If it\Picked > 0 Then HideEntity(it\collider)
		
		nt = ReadByte(f)
		If nt = True Then SelectedItem = it
		
		nt = ReadByte(f)
		If nt < 66
			Inventory(nt) = it
			ItemAmount = ItemAmount + 1
		EndIf
		
		For itt.ItemTemplates = Each ItemTemplates
			If itt\tempname = tempName Then
				If itt\isAnim<>0 Then SetAnimTime it\model,ReadFloat(f) : Exit
			EndIf
		Next
		it\invSlots = ReadByte(f)
		it\ID = ReadInt(f)
		
		If it\ID>LastItemID Then LastItemID=it\ID
		
		If ReadByte(f)=0 Then
			it\invimg=it\itemtemplate\invimg
		Else
			it\invimg=it\itemtemplate\invimg2
		EndIf
	Next	
	
	Local o_i%
	
	temp = ReadInt(f)
	For i=1 To temp
		;OtherInv
		o_i=ReadInt(f)
		
		For ij.Items = Each Items
			If ij\ID=o_i Then it.Items=ij : Exit
		Next
		For j%=0 To it\invSlots-1
			o_i=ReadInt(f)
			If o_i<>-1 Then
				For ij.Items=Each Items
					If ij\ID=o_i Then
						it\SecondInv[j]=ij
						Exit
					EndIf
				Next
			EndIf
		Next
		;OtherInv End
	Next
	For itt.ItemTemplates = Each ItemTemplates
		itt\found = ReadByte(f)
	Next
	
	For do.Doors = Each Doors
		If do\room <> Null Then
			dist# = 20.0
			Local closestroom.Rooms
			For r.Rooms = Each Rooms
				dist2# = EntityDistance(r\obj, do\obj)
				If dist2 < dist Then
					dist = dist2
					closestroom = r.Rooms
				EndIf
			Next
			do\room = closestroom
		EndIf
	Next
	
	;If ReadInt(f) <> 994 Then RuntimeError("Couldn't load the game, save file corrupted (error 4)")
	
	If ReadInt(f)<>994
		UsedConsole = True
		DebugLog "Used Console"
	EndIf
	
	If 0 Then 
		closestroom = Null
		dist = 30
		For r.Rooms = Each Rooms
			dist2# = EntityDistance(r\obj, Collider)
			If dist2 < dist Then
				dist = dist2
				closestroom = r
			EndIf
		Next
		
		If closestroom<>Null Then PlayerRoom = closestroom
	EndIf
	
	;This will hopefully fix the 895 crash bug after the player died by it's sanity effect and then quickloaded the game - ENDSHN
	For sc.SecurityCams = Each SecurityCams
		sc\PlayerState = 0
	Next
	EntityTexture NVOverlay,NVTexture
	RestoreSanity = True
	
	CameraFogFar = ReadFloat(f)
	StoredCameraFogFar = ReadFloat(f)
	If CameraFogFar = 0 Then
		CameraFogFar = 6
	EndIf
	
	Local I_427.SCP427 = First SCP427
	I_427\Using = ReadByte(f)
	I_427\Timer = ReadFloat(f)
	
	Wearing714 = ReadByte(f)
	CloseFile f
	
	If Collider <> 0 Then
		If PlayerRoom<>Null Then
			ShowEntity PlayerRoom\obj
		EndIf
		ShowEntity Collider
		TeleportEntity(Collider,EntityX(Collider),EntityY(Collider)+0.5,EntityZ(Collider),0.3,True)
		If PlayerRoom<>Null Then
			HideEntity PlayerRoom\obj
		EndIf
	EndIf
	
	UpdateDoorsTimer = 0
	
	;Free some entities that could potentially cause memory leaks (for the endings)
	;This is only required for the LoadGameQuick function, as the other one is from the menu where everything is already deleted anyways
	Local xtemp#,ztemp#
	If Sky <> 0 Then
		FreeEntity Sky
		Sky = 0
	EndIf
	For r.Rooms = Each Rooms
		If r\RoomTemplate\Name = "gatea" Then
			If r\Objects[0]<>0 Then
				FreeEntity r\Objects[0] : r\Objects[0] = 0
				xtemp#=EntityX(r\Objects[9],True)
				ztemp#=EntityZ(r\Objects[9],True)
				FreeEntity r\Objects[9] : r\Objects[9] = 0
				r\Objects[10] = 0 ;r\Objects[10] is already deleted because it is a parent object to r\Objects[9] which is already deleted a line before
				;Readding this object, as it is originally inside the "FillRoom" function but gets deleted when it loads GateA
				r\Objects[9]=CreatePivot()
				PositionEntity(r\Objects[9], xtemp#, r\y+992.0*RoomScale, ztemp#, True)
				EntityParent r\Objects[9], r\obj
				;The GateA wall pieces
				xtemp# = EntityX(r\Objects[13],True)
				ztemp# = EntityZ(r\Objects[13],True)
				FreeEntity r\Objects[13]
				r\Objects[13]=LoadMesh_Strict("GFX\map\gateawall1.b3d",r\obj)
				PositionEntity(r\Objects[13], xtemp#, r\y-1045.0*RoomScale, ztemp#, True)
				EntityColor r\Objects[13], 25,25,25
				EntityType r\Objects[13],HIT_MAP
				xtemp# = EntityX(r\Objects[14],True)
				ztemp# = EntityZ(r\Objects[14],True)
				FreeEntity r\Objects[14]
				r\Objects[14]=LoadMesh_Strict("GFX\map\gateawall2.b3d",r\obj)
				PositionEntity(r\Objects[14], xtemp#, r\y-1045.0*RoomScale, ztemp#, True)	
				EntityColor r\Objects[14], 25,25,25
				EntityType r\Objects[14],HIT_MAP
			EndIf
			If r\Objects[12]<>0 Then
				FreeEntity r\Objects[12] : r\Objects[12] = 0
				FreeEntity r\Objects[17] : r\Objects[17] = 0
			EndIf
		ElseIf r\RoomTemplate\Name = "gateb" Then
			If r\Objects[0]<>0 Then
				xtemp# = EntityX(r\Objects[0],True)
				ztemp# = EntityZ(r\Objects[0],True)
				FreeEntity r\Objects[0] : r\Objects[0] = 0
				r\Objects[0] = CreatePivot(r\obj)
				PositionEntity(r\Objects[0], xtemp#, 9767.0*RoomScale, ztemp#, True)
			EndIf
		EndIf
	Next
	;Resetting some stuff (those get changed when going to the endings)
	CameraFogMode(Camera, 1)
	HideDistance# = 15.0
	
	;CatchErrors("Uncaught LoadGameQuick")
End Function

Function LoadSaveGames()
	;CatchErrors("LoadSaveGames")
	SaveGameAmount = 0
	If FileType(SavePath)=1 Then RuntimeError "Can't create dir "+Chr(34)+SavePath+Chr(34)
	If FileType(SavePath)=0 Then CreateDir(SavePath)
	myDir=ReadDir(SavePath) 
	Repeat 
		file$=NextFile$(myDir) 
		If file$="" Then Exit 
		If FileType(SavePath+"\"+file$) = 2 Then 
			If file <> "." And file <> ".." Then 
				If (FileType(SavePath + file + "\save.txt")>0) Then
					SaveGameAmount=SaveGameAmount+1
				EndIf
			EndIf
		EndIf 
	Forever 
	CloseDir myDir 
	
	Dim SaveGames$(SaveGameAmount+1) 
	
	myDir=ReadDir(SavePath) 
	i = 0
	Repeat 
		file$=NextFile$(myDir) 
		If file$="" Then Exit 
		If FileType(SavePath+"\"+file$) = 2 Then 
			If file <> "." And file <> ".." Then 
				If (FileType(SavePath + file + "\save.txt")>0) Then
					SaveGames(i) = file
					i=i+1
				EndIf
			EndIf
		EndIf 
	Forever 
	CloseDir myDir 
	
	Dim SaveGameTime$(SaveGameAmount + 1)
	Dim SaveGameDate$(SaveGameAmount + 1)
	Dim SaveGameVersion$(SaveGameAmount + 1)
	Dim SaveGameLang$(SaveGameAmount + 1)
	For i = 1 To SaveGameAmount
		DebugLog (SavePath + SaveGames(i - 1) + "\save.txt")
		Local f% = ReadFile(SavePath + SaveGames(i - 1) + "\save.txt")
		SaveGameTime(i - 1) = ReadString(f)
		SaveGameDate(i - 1) = ReadString(f)
		;Skip all data until the CompatibleVersion number
		ReadInt(f)
		For j = 0 To 5
			ReadFloat(f)
		Next
		ReadString(f)
		ReadFloat(f)
		ReadFloat(f)
		;End Skip
		SaveGameVersion(i - 1) = ReadString(f)
		SaveGameLang(i - 1) = ReadString(f)
		CloseFile f
	Next
	
	;CatchErrors("Uncaught LoadSaveGames")
End Function