Function Use914(item.Items, setting$, x#, y#, z#)
	
	RefinedItems = RefinedItems+1
	
	Local it2.Items
	Local remove% = 1
	Select item\itemtemplate\tempname
		Case "key1", "key2", "key3", "key4", "key5"
			Local level% = Right(item\itemtemplate\tempname, 1)
			Select setting
				Case "rough"
					If Rand(Right(item\itemtemplate\tempname, 1)) = 1
						d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
						d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
					Else
						it2 = CreateItem("key1", x, y, z)
					EndIf
				Case "coarse"
					If level = 1 Then
						d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
						d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
					Else
						it2 = CreateItem("key" + (level - 1), x, y, z)
					EndIf
				Case "1:1"
					it2 = CreateItem("misc", x, y, z, "keyplay")
				Case "fine"
					Select level
						Case 1
							Select SelectedDifficulty\otherFactors
								Case EASY
									it2 = CreateItem("key2", x, y, z)
								Case NORMAL
									If Rand(5)=1 Then
										it2 = CreateItem("misc", x, y, z, "keymaster")
									Else
										it2 = CreateItem("key2", x, y, z)
									EndIf
								Case HARD
									If Rand(4)=1 Then
										it2 = CreateItem("misc", x, y, z, "keymaster")
									Else
										it2 = CreateItem("key2", x, y, z)
									EndIf
								Case EXTREME
									If Rand(3)=1 Then
										it2 = CreateItem("misc", x, y, z, "keymaster")
									Else
										it2 = CreateItem("key2", x, y, z)
									EndIf
							End Select
						Case 2
							Select SelectedDifficulty\otherFactors
								Case EASY
									it2 = CreateItem("key3", x, y, z)
								Case NORMAL
									If Rand(4)=1 Then
										it2 = CreateItem("misc", x, y, z, "keymaster")
									Else
										it2 = CreateItem("key3", x, y, z)
									EndIf
								Case HARD
									If Rand(3)=1 Then
										it2 = CreateItem("misc", x, y, z, "keymaster")
									Else
										it2 = CreateItem("key3", x, y, z)
									EndIf
								Case EXTREME
									If Rand(2)=1 Then
										it2 = CreateItem("misc", x, y, z, "keymaster")
									Else
										it2 = CreateItem("key3", x, y, z)
									EndIf
							End Select
						Case 3
							Select SelectedDifficulty\otherFactors
								Case EASY
									If Rand(10)=1 Then
										it2 = CreateItem("key4", x, y, z)
									Else
										it2 = CreateItem("misc", x, y, z, "keyplay")	
									EndIf
								Case NORMAL
									If Rand(15)=1 Then
										it2 = CreateItem("key4", x, y, z)
									Else
										it2 = CreateItem("misc", x, y, z, "keyplay")	
									EndIf
								Case HARD
									If Rand(20)=1 Then
										it2 = CreateItem("key4", x, y, z)
									Else
										it2 = CreateItem("misc", x, y, z, "keyplay")	
									EndIf
								Case EXTREME
									If Rand(30)=1 Then
										it2 = CreateItem("key4", x, y, z)
									Else
										it2 = CreateItem("misc", x, y, z, "keyplay")	
									EndIf
							End Select
						Case 4
							Select SelectedDifficulty\otherFactors
								Case EASY
									it2 = CreateItem("key5", x, y, z)
								Case NORMAL
									If Rand(4)=1 Then
										it2 = CreateItem("misc", x, y, z, "keymaster")
									Else
										it2 = CreateItem("key5", x, y, z)
									EndIf
								Case HARD
									If Rand(3)=1 Then
										it2 = CreateItem("misc", x, y, z, "keymaster")
									Else
										it2 = CreateItem("key5", x, y, z)
									EndIf
								CASE EXTREME
									If Rand(2)=1 Then
										it2 = CreateItem("misc", x, y, z, "keymaster")
									Else
										it2 = CreateItem("key5", x, y, z)
									EndIf
							End Select
						Case 5	
							Local CurrAchvAmount%=0
							For i = 0 To MAXACHIEVEMENTS
								If Achievements[i]=True
									CurrAchvAmount=CurrAchvAmount+1
								EndIf
							Next
							
							Select SelectedDifficulty\otherFactors
								Case EASY
									If Rand(0,((MAXACHIEVEMENTS)*3)-((CurrAchvAmount-1)*3))=0
										it2 = CreateItem("keyomni", x, y, z)
									Else
										it2 = CreateItem("misc", x, y, z, "keymaster")
									EndIf
								Case NORMAL
									If Rand(0,((MAXACHIEVEMENTS)*4)-((CurrAchvAmount-1)*3))=0
										it2 = CreateItem("keyomni", x, y, z)
									Else
										it2 = CreateItem("misc", x, y, z, "keymaster")
									EndIf
								Case HARD
									If Rand(0,((MAXACHIEVEMENTS)*5)-((CurrAchvAmount-1)*3))=0
										it2 = CreateItem("keyomni", x, y, z)
									Else
										it2 = CreateItem("misc", x, y, z, "keymaster")
									EndIf
								Case EXTREME
									If Rand(0,((MAXACHIEVEMENTS)*6)-((CurrAchvAmount-1)*3))=0
										it2 = CreateItem("keyomni", x, y, z)
									Else
										it2 = CreateItem("misc", x, y, z, "keymaster")
									EndIf
							End Select		
					End Select
				Case "very fine"
					CurrAchvAmount%=0
					For i = 0 To MAXACHIEVEMENTS
						If Achievements[i]=True
							CurrAchvAmount=CurrAchvAmount+1
						EndIf
					Next
					
					Select SelectedDifficulty\otherFactors
						Case EASY
							If Rand(0,((MAXACHIEVEMENTS)*3)-((CurrAchvAmount-1)*3))=0
								it2 = CreateItem("keyomni", x, y, z)
							Else
								it2 = CreateItem("misc", x, y, z, "keymaster")
							EndIf
						Case NORMAL
							If Rand(0,((MAXACHIEVEMENTS)*4)-((CurrAchvAmount-1)*3))=0
								it2 = CreateItem("keyomni", x, y, z)
							Else
								it2 = CreateItem("misc", x, y, z, "keymaster")
							EndIf
						Case HARD
							If Rand(0,((MAXACHIEVEMENTS)*5)-((CurrAchvAmount-1)*3))=0
								it2 = CreateItem("keyomni", x, y, z)
							Else
								it2 = CreateItem("misc", x, y, z, "keymaster")
							EndIf
						Case EXTREME
							If Rand(0,((MAXACHIEVEMENTS)*6)-((CurrAchvAmount-1)*3))=0
								it2 = CreateItem("keyomni", x, y, z)
							Else
								it2 = CreateItem("misc", x, y, z, "keymaster")
							EndIf
					End Select
			End Select
		Case "keyomni"
			Select setting
				Case "rough"
					If Rand(2)=1 Then
						d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
						d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
					Else
						it2 = CreateItem("key1", x, y, z)
					EndIf
				Case "coarse"
					If Rand(3)=1 Then
						d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
						d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
					Else
						it2 = CreateItem("key5", x, y, z)
					EndIf
				Case "1:1"
					If Rand(3)=1 Then
						If Rand(2)=1 Then
							it2 = CreateItem("misc", x, y, z, "keymaster")
						Else
							it2 = CreateItem("misc", x, y, z, "keyplay")
						EndIf
					Else
						it2 = CreateItem("key5", x, y, z)
					EndIf
				Case "fine", "very fine"
					it2 = CreateItem("misc", x, y, z, "keymaster")
			End Select
		Case "scp148"
			Select setting
				Case "rough", "coarse"
					it2 = CreateItem("scp148ingot", x, y, z)
				Case "1:1", "fine", "very fine"
					remove = 0
			End Select
		Case "scp148ingot"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.35 : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1"
					remove = 0
				Case "fine", "very fine"
					For it.Items = Each Items
						If it<>item And it\collider <> 0 And it\Picked = 0 Then
							If Distance(EntityX(it\collider,True), x, EntityZ(it\collider,True), z) < (180.0 * RoomScale)
								Select it\itemtemplate\tempname
									Case "badgasmask", "gasmask", "supergasmask"
										it2 = CreateItem("heavygasmask", x, y, z)
										Exit
									Case "vest"
										it2 = CreateItem("finevest", x, y, z)
										Exit
									Case "hazmat","hazmat2"
										it2 = CreateItem("hazmat3", x, y, z)
										Exit
								End Select
							EndIf
						EndIf
					Next
					
					If it2 = Null Then
						it2 = CreateItem("scp148", x, y, z)
					EndIf
			End Select
		Case "scp178"
			Select setting
				Case "rough"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
					For n.NPCs = Each NPCs
						If n\NPCtype = NPCtype178 Then RemoveNPC(n)
					Next
				Case "coarse"
					item\State = -2
					remove = 0
				Case "1:1"
					remove = 0
				Case "fine"
					item\State = 1
					remove = 0
				Case "very fine"
					n.NPCs = CreateNPC(NPCtype178,x,y,z)
					n\State3 = 1 ;I don't think they should be aggressive right away, this way it doesn't force the player to reset, resulting in a higher propability of accepting the loss of SCP-178, which is preferable.
			End Select
		Case "scp420j"
			Select setting
				Case "rough"		
					d.Decals = CreateDecal(0, x, 8*RoomScale+0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
				Case "coarse"
					it2 = CreateItem("cigarette", x, y, z)
				Case "1:1"
					it2 = CreateItem("420s", x, y, z, "joint")
				Case "fine", "very fine"
					it2 = CreateItem("420s", x, y, z, "smellyjoint")
			End Select
		Case "cigarette"
			Select setting
				Case "rough", "coarse"	
					d.Decals = CreateDecal(0, x, 8*RoomScale+0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1"
					remove = 0
				Case "fine"
					If Rand(3) = 1 Then
						it2 = CreateItem("scp420j", x, y, z)
					Else
						it2 = CreateItem("420s", x, y, z, "joint")
					EndIf
				Case "very fine"
					it2 = CreateItem("420s", x, y, z, "smellyjoint")
			End Select
		Case "420s"
			If item\itemtemplate\namespec = "joint" Then
				Select setting
					Case "rough"
						d.Decals = CreateDecal(0, x, 8*RoomScale+0.010, z, 90, Rand(360), 0)
						d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
					Case "coarse"
						it2 = CreateItem("cigarette", x, y, z)
					Case "1:1"
						If Rand(3) = 1 Then
							it2 = CreateItem("scp420j", x, y, z)
						Else
							remove = 0
						EndIf
					Case "fine", "very fine"
						it2 = CreateItem("420s", x, y, z, "smellyjoint")
				End Select
			Else ;smellyjoint
				Select setting
					Case "rough"
						it2 = CreateItem("cigarette", x, y, z)
					Case "coarse"
						If Rand(3) = 1 Then
							it2 = CreateItem("scp420j", x, y, z)
						Else
							it2 = CreateItem("420s", x, y, z, "joint")
						EndIf
					Case "1:1", "fine", "very fine"
						remove = 0
				End Select
			EndIf
		Case "scp427"
			Select setting
				Case "rough"
					d.Decals = CreateDecal(0, x, 8*RoomScale+0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
				Case "coarse"
					it2 = CreateItem("scp500", x, y, z)
				Case "1:1"
					remove = 0
				Case "fine", "very fine"
					it2 = CreateItem("super427", x, y, z)
			End Select
		Case "super427"
			Select setting
				Case "rough"
					it2 = CreateItem("scp500", x, y, z)
				Case "coarse"
					it2 = CreateItem("scp427", x, y, z)
				Case "1:1", "fine", "very fine"
					remove = 0
			End Select
		Case "scp500"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1"
					it2 = CreateItem("pill", x, y, z)
				Case "fine"
					Local I_427.SCP427 = First SCP427
					If (Rand(I_427\Amount + 1) = 1) Then
						it2 = CreateItem("scp427", x, y, z)
						I_427\Amount = I_427\Amount + 1
					Else
						it2 = CreateItem("upgradedpill", x, y, z)
					EndIf
				Case "very fine"
					it2 = CreateItem("upgradedpill", x, y, z)
			End Select
		Case "upgradedpill"
			Select setting
				Case "rough"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
				Case "coarse"
					If Rand(3) = 1 Then
						it2 = CreateItem("scp500", x, y, z)
					Else
						it2 = CreateItem("pill", x, y, z)
					EndIf
				Case "1:1", "fine", "very fine"
					remove = 0
			End Select
		Case "pill"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1"
					If Rand(3) = 1 Then
						it2 = CreateItem("scp500", x, y, z)
					Else
						remove = 0
					EndIf
				Case "fine", "very fine"
					it2 = CreateItem("upgradedpill", x, y, z)
			End Select
		Case "scp513"
			Select setting
				Case "rough","coarse"
					PlaySound_Strict LoadTempSound("SFX\SCP\513\914Refine.ogg")
					For n.npcs = Each NPCs
						If item\state <> 0 And item\state = n\ID Then RemoveNPC(n)
					Next
					d.Decals = CreateDecal(0, x, 8*RoomScale+0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1","fine","very fine"
					remove = 0
			End Select
		Case "scp714"
			remove = 0
		Case "scp860"
			Select setting
				Case "rough","coarse"
					d.Decals = CreateDecal(0, x, 8*RoomScale+0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1", "fine", "very fine"
					If Rand(10) = 1 Then
						it2 = CreateItem("key", x, y, z)
					Else
						remove = 0
					EndIf
			End Select
		Case "scp1025"
			Select setting
				Case "rough"
					If item\state2 > 0 Then
						item\state2 = -1
					Else
						d.Decals = CreateDecal(0, x, 8*RoomScale+0.010, z, 90, Rand(360), 0)
						d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
					EndIf
				Case "coarse"
					item\state2 = Max(-1, item\state2 - 1)
					remove = 0
				Case "1:1"
					it2 = CreateItem("book", x, y, z)
				Case "fine"
					item\state2 = Min(1, item\state2 + 1)
					remove = 0
				Case "very fine"
					item\state2 = 2
					remove = 0
			End Select
		Case "book"
			Select setting
				Case "rough","coarse"
					d.Decals = CreateDecal(0, x, 8*RoomScale+0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1"
					If Rand(3) = 1 Then
						it2 = CreateItem("scp1025", x, y, z) ; I know that this can be exploited to get a 1025 reset, but this effort makes it seem fair to me
					Else
						remove = 0
					EndIf
				Case "fine","veryfine"
					remove = 0
			End Select
		;Case "scp1123" ;no idea
		Case "bad1499"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1"
					it2 = CreateItem("badgasmask", x, y, z)
				Case "fine"
					it2 = CreateItem("scp1499", x, y, z)
				Case "very fine"
					it2 = CreateItem("super1499", x, y, z)
			End Select
		Case "scp1499"
			Select setting
				Case "rough"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
				Case "coarse"
					it2 = CreateItem("bad1499", x, y, z)
				Case "1:1"
					it2 = CreateItem("gasmask", x, y, z)
				Case "fine"
					it2 = CreateItem("super1499", x, y, z)
				Case "very fine"
					If Rand(3)=1 Then
						n.NPCs = CreateNPC(NPCtype1499,x,y,z)
						n\State = 1
						n\Sound = LoadSound_Strict("SFX\SCP\1499\Triggered.ogg")
						n\SoundChn = PlaySound2(n\Sound, Camera, n\Collider,20.0)
						n\State3 = 1
					Else
						it2 = CreateItem("fine1499", x, y, z)
					EndIf
			End Select
		Case "super1499"
			Select setting
				Case "rough"
					it2 = CreateItem("bad1499", x, y, z)
				Case "coarse"
					it2 = CreateItem("scp1499", x, y, z)
				Case "1:1"
					it2 = CreateItem("supergasmask", x, y, z)
				Case "fine", "very fine"
					If Rand(5)=1 Then
						n.NPCs = CreateNPC(NPCtype1499,x,y,z)
						n\State = 1
						n\Sound = LoadSound_Strict("SFX\SCP\1499\Triggered.ogg")
						n\SoundChn = PlaySound2(n\Sound, Camera, n\Collider,20.0)
						n\State3 = 1
					Else
						it2 = CreateItem("fine1499", x, y, z)
					EndIf
			End Select
		Case "fine1499"
			Select setting
				Case "rough"
					it2 = CreateItem("scp1499", x, y, z)
				Case "coarse"
					it2 = CreateItem("super1499", x, y, z)
				Case "1:1"
					it2 = CreateItem("heavygasmask", x, y, z)
				Case "fine", "very fine"
					n.NPCs = CreateNPC(NPCtype1499,x,y,z)
					n\State = 1
					n\Sound = LoadSound_Strict("SFX\SCP\1499\Triggered.ogg")
					n\SoundChn = PlaySound2(n\Sound, Camera, n\Collider,20.0)
					n\State3 = 1
			End Select
		Case "paper"
			Select setting
				Case "rough"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.2 : ScaleSprite(d\obj, d\Size, d\Size)
				Case "coarse"
					d.Decals = CreateDecal(7, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1"
					it2 = CreateItem("paper", x, y, z, GetPaper(item))
				Case "fine", "very fine"
					it2 = CreateItem("misc", x,	y, z, "origami")
					If item\itemtemplate\namespec = "dblank" Then
						EntityTexture(it2\model, LoadTexture_Strict("GFX\items\docBlank.jpg"))
						it2\State = 1
					EndIf
			End Select
		Case "misc"
			Local temp%
			Select item\itemtemplate\namespec
				Case "keymaster"
					Select setting
						Case "rough"
							d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
						Case "coarse"
							it2 = CreateItem("quarter", x, y, z)
							Local it3.Items,it4.Items,it5.Items
							it3 = CreateItem("quarter", x, y, z)
							it4 = CreateItem("quarter", x, y, z)
							it5 = CreateItem("quarter", x, y, z)
							EntityType (it3\collider, HIT_ITEM)
							EntityType (it4\collider, HIT_ITEM)
							EntityType (it5\collider, HIT_ITEM)
						Case "1:1"
							If Rand(100) = 1 Then
								it2 = CreateItem("keyomni", x, y, z)
							Else
								it2 = CreateItem("key1", x, y, z)
							EndIf
						Case "fine", "very fine"
							temp% = Rand(1000)
							If temp < 330 Then
								it2 = CreateItem("key1", x, y, z)
							ElseIf temp < 660
								it2 = CreateItem("key2", x, y, z)
							ElseIf temp < 990
								it2 = CreateItem("key3", x, y, z)
							ElseIf temp < 998
								it2 = CreateItem("key4", x, y, z)
							ElseIf temp < 1000
								it2 = CreateItem("key5", x, y, z)
							Else ;If temp = 1000
								it2 = CreateItem("keyomni", x, y, z)
							EndIf
					End Select
				Case "keyplay"
					Select setting
						Case "rough", "coarse"
							d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
						Case "1:1", "fine"
							it2 = CreateItem("key1", x, y, z)	
						Case "very fine"
							temp% = Rand(1000)
							If temp < 330 Then
								it2 = CreateItem("key1", x, y, z)
							ElseIf temp < 660
								it2 = CreateItem("key2", x, y, z)
							ElseIf temp < 990
								it2 = CreateItem("key3", x, y, z)
							ElseIf temp < 998
								it2 = CreateItem("key4", x, y, z)
							ElseIf temp < 1000
								it2 = CreateItem("key5", x, y, z)
							Else ;If temp = 1000
								it2 = CreateItem("keyomni", x, y, z)
							EndIf
					End Select
				Case "origami"
					Select setting
						Case "rough"
							d.Decals = CreateDecal(7, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
						Case "coarse"
							it2 = CreateItem("paper", x, y, z, GetPaper(item))
						Case "1:1", "fine", "very fine"
							remove = 0
					End Select
				Case "electronical"
					Select setting
						Case "rough", "coarse"
							d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							d\Size = 0.15 : ScaleSprite(d\obj, d\Size, d\Size)
						Case "1:1"
							remove = 0
						Case "fine"
							Select Rand(3)
								Case 1
									it2 = CreateItem("radio", x, y, z)
								Case 2
									If Rand(2) = 1 Then
										it2 = CreateItem("nav", x, y, z)
									Else
										it2 = CreateItem("nav300", x, y, z)
									EndIf
								Case 3
									it2 = CreateItem("nvg", x, y, z)
							End Select
						Case "very fine"
							Select Rand(3)
								Case 1
									If Rand(3) = 1 Then
										it2 = CreateItem("fineradio", x, y, z)
									Else
										it2 = Createitem("veryfineradio", x, y, z)
									EndIf
								Case 2
									it2 = CreateItem("navulti", x, y, z)
								Case 3
									If Rand(2) = 1 Then
										it2 = CreateItem("finenvg", x, y, z)
									Else
										it2 = CreateItem("supernvg", x, y, z)
									EndIf
							End Select
					End Select
				Default
					Select setting
						Case "rough","coarse"
							d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
							d\Size = 0.2 : ScaleSprite(d\obj, d\Size, d\Size)
						Case "1:1","fine","very fine"
							remove = 0
					End Select
			End Select
		Case "badge"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1", "fine", "very fine"
					If item\itemtemplate\namespec = "" Then
						If Rand(100) Then
							it2 = CreateItem("badge", x, y, z, "oldbadge")
						Else
							remove = 0
						EndIf
					Else
						it2 = CreateItem("badge", x, y, z)
					EndIf
			End Select
		Case "badgasmask"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.11 : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1"
					If Rand(25) = 1 Then
						it2 = CreateItem("bad1499", x, y, z)
					Else
						remove = 0
					EndIf
				Case "fine"
					it2 = CreateItem("gasmask", x, y, z)
				Case "very fine"
					it2 = CreateItem("supergasmask", x, y, z)
			End Select
		Case "gasmask"
			Select setting
				Case "rough"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
				Case "coarse"
					it2 = CreateItem("badgasmask", x, y, z)
				Case "1:1"
					If Rand(50) = 1 Then
						it2 = CreateItem("scp1499", x, y, z)
					Else
						remove = 0
					EndIf
				Case "fine", "very fine"
					it2 = CreateItem("supergasmask", x, y, z)
			End Select
		Case "supergasmask", "heavygasmask"
			Select setting
				Case "rough"
					it2 = CreateItem("badgasmask", x, y, z)
				Case "coarse"
					it2 = CreateItem("gasmask", x, y, z)
				Case "1:1"
					If Rand(100) = 1 Then
						If Rand(3) = 1 Then
							it2 = CreateItem("super1499", x, y, z)
						Else
							it2 = CreateItem("fine1499", x, y, z)
						EndIf
					Else
						remove = 0
					EndIf
				Case "fine", "very fine"
					If item\itemtemplate\tempname = "supergasmask" Then
						remove = 0
					Else
						it2 = CreateItem("supergasmask", x, y, z)
					EndIf
			End Select
		Case "hazmat0"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
					d\Size = 0.3 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1"
					remove = 0
				Case "fine"
					it2 = CreateItem("hazmat", x, y, z)
				Case "very fine"
					it2 = CreateItem("hazmat2", x,y,z)
			End Select
		Case "hazmat"
			Select setting
				Case "rough"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
					d\Size = 0.3 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
				Case "coarse"
					it2 = CreateItem("hazmat0", x, y, z)
				Case "1:1"
					remove = 0
				Case "fine", "very fine"
					it2 = CreateItem("hazmat2", x, y, z)
			End Select
		Case "hazmat2", "hazmat3"
			Select setting
				Case "rough"
					it2 = CreateItem("hazmat0", x, y, z)
				Case "coarse"
					it2 = CreateItem("hazmat", x, y, z)
				Case "1:1", "fine", "very fine"
					If item\itemtemplate\tempname = "hazmat3" Then
						it2 = CreateItem("hazmat2", x, y, z)
					Else
						remove = 0
					EndIf
			End Select
		Case "badnvg"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1"
					remove = 0
				Case "fine"
					it2 = CreateItem("nvg", x, y, z)
					it2\state = item\state
				Case "very fine"
					it2 = CreateItem("finenvg", x, y, z)
			End Select
		Case "nvg"
			Select setting
				Case "rough"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
				Case "coarse"
					it2 = CreateItem("badnvg", x, y, z)
					it2\state = item\state/2
				Case "1:1"
					remove = 0
				Case "fine"
					it2 = CreateItem("finenvg", x, y, z)
				Case "very fine"
					it2 = CreateItem("supernvg", x, y, z)
					it2\state = item\state
			End Select
		Case "finenvg"
			Select setting
				Case "rough"
					it2 = CreateItem("badnvg", x, y, z)
					it2\state = 0
				Case "coarse"
					it2 = CreateItem("nvg", x, y, z)
					it2\state = 1000
				Case "1:1"
					remove = 0
				Case "fine", "very fine"
					it2 = CreateItem("supernvg", x, y, z)
					it2\state = 1000
			End Select
		Case "supernvg"
			Select setting
				Case "rough"
					it2 = CreateItem("nvg", x, y, z)
					it2\state = item\state
				Case "coarse"
					it2 = CreateItem("finenvg", x, y, z)
				Case "1:1", "fine", "very fine"
					remove = 0
			End Select
		Case "nav300", "nav"
			Select setting
				Case "rough", "coarse"
					it2 = CreateItem("misc", x, y, z, "electronical")
				Case "1:1"
					If item\itemtemplate\tempname = "nav" Then
						it2 = CreateItem("nav", x, y, z)
					Else
						it2 = CreateItem("nav300", x, y, z)
					EndIf
					it2\state = item\state
				Case "fine"
					it2 = CreateItem("nav310", x, y, z)
					it2\state = Min(100, item\state * 2)
				Case "very fine"
					it2 = CreateItem("navulti", x, y, z)
			End Select
		Case "nav310"
			Select setting
				Case "rough"
					it2 = CreateItem("misc", x, y, z, "electronical")
				Case "coarse"
					If Rand(3) = 1 Then
						it2 = CreateItem("nav", x, y, z)
					Else
						it2 = CreateItem("nav300", x, y, z)
					EndIf
					it2\state = Min(100, item\state * 2)
				Case "1:1"
					remove = 0
				Case "fine", "veryfine"
					it2 = CreateItem("navulti", x, y, z)
			End Select
		Case "navulti"
			Select setting
				Case "rough"
					If Rand(2) = 1 Then
						it2 = CreateItem("nav", x, y, z)
					Else
						it2 = CreateItem("nav300", x, y, z)
					EndIf
					it2\state = 0
				Case "coarse"
					it2 = CreateItem("nav310", x, y, z)
					it2\state = 100
				Case "1:1", "fine", "very fine"
					remove = 0
			End Select
		Case "radio"
			Select setting
				Case "rough", "coarse"
					it2 = CreateItem("misc", x, y, z, "electronical")
				Case "1:1"
					it2 = CreateItem("18vradio", x, y, z)
					it2\state = item\state
				Case "fine"
					it2 = CreateItem("fineradio", x, y, z)
					it2\state = 101
				Case "very fine"
					it2 = CreateItem("veryfineradio", x, y, z)
					it2\state = 101
			End Select
		Case "fineradio"
			Select setting
				Case "rough"
					it2 = CreateItem("misc", x, y, z, "electronical")
				Case "coarse"
					If Rand(3) = 1 Then
						it2 = CreateItem("18vradio", x, y, z)
					Else
						it2 = CreateItem("radio", x, y, z)
					EndIf
					it2\state = 100
				Case "1:1"
					remove = 0
				Case "fine", "very fine"
					it2 = CreateItem("veryfineradio", x, y, z)
					it2\state = 101
			End Select
		Case "veryfineradio"
			Select setting
				Case "rough"
					If Rand(2) = 1 Then
						it2 = CreateItem("18vradio", x, y, z)
					Else
						it2 = CreateItem("radio", x, y, z)
					EndIf
					it2\state = 0
				Case "coarse"
					it2 = CreateItem("fineradio", x, y, z)
					it2\state = 101
				Case "1:1", "fine", "very fine"
					remove = 0
			End Select
		Case "bat"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1"
					remove = 0
				Case "fine"
					it2 = CreateItem("18vbat", x, y, z)
				Case "very fine"
					it2 = CreateItem("killbat", x, y, z)
			End Select
		Case "18vbat"
			Select setting
				Case "rough"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
				Case "coarse"
					it2 = CreateItem("bat", x, y, z)
				Case "1:1"
					remove = 0
				Case "fine", "very fine"
					it2 = CreateItem("killbat", x, y, z)
			End Select
		;Case "killbat" ;doesn't need refinements
		Case "badvest"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1"
					remove = 0
				Case "fine"
					it2 = CreateItem("vest", x, y, z)
				Case "very fine"
					it2 = CreateItem("finevest", x, y, z)
			End Select
		Case "vest"
			Select setting
				Case "rough"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
				Case "coarse"
					it2 = CreateItem("badvest", x, y, z)
				Case "1:1"
					remove = 0
				Case "fine"
					it2 = CreateItem("finevest", x, y, z)
				Case "very fine"
					it2 = CreateItem("veryfinevest", x, y, z)
			End Select
		Case "finevest"
			Select setting
				Case "rough"
					it2 = CreateItem("badvest", x, y, z)
				Case "coarse"
					it2 = CreateItem("vest", x, y, z)
				Case "1:1"
					remove = 0
				Case "fine", "very fine"
					it2 = CreateItem("veryfinevest", x, y, z)
			End Select
		;Case "veryfinevest" ;doesn't need refinements
		Case "cup"
			Select setting
				Case "rough"
					If item\state > 0 Then
						item\state = -3
						remove = 0
					EndIf
				Case "coarse"
					If item\state > -3 Then
						item\state = item\state - 1
						item\r = item\r*0.9
						item\g = item\r*0.9
						item\b = item\r*0.9
						remove = 0
					EndIf
				Case "1:1"
					item\state = -item\state
					item\r = 255-item\r
					item\g = 255-item\g
					item\b = 255-item\b
					remove = 0
				Case "fine"
					item\state = Min(3, item\state + 1)
					item\r = Min(item\r*1.1,255)
					item\g = Min(item\g*1.1,255)
					item\b = Min(item\b*1.1,255)
					remove = 0
				Case "very fine"
					item\state = 3
					item\r = Min(item\r*1.5,255)
					item\g = Min(item\g*1.5,255)
					item\b = Min(item\b*1.5,255)
					If Rand(5)=1 Then
						ExplosionTimer = 135
					EndIf
					remove = 0
			End Select
		;Case "emptycup" ;no idea, fill it would cause issues with localization
		Case "firstaid", "firstaid2"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1"
					If item\itemtemplate\tempname = "firstaid" Then
						it2 = CreateItem("firstaid2", x, y, z)
					Else
						it2 = CreateItem("firstaid", x, y, z)
					EndIf
				Case "fine"
					it2 = CreateItem("finefirstaid", x, y, z)
				Case "very fine"
					it2 = CreateItem("veryfinefirstaid", x, y, z)
			End Select
		Case "finefirstaid"
			Select setting
				Case "rough"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
				Case "coarse"
					If Rand(3) = 1 Then
						it2 = CreateItem("firstaid", x, y, z)
					Else
						it2 = CreateItem("firstaid2", x, y, z)
					EndIf
				Case "1:1"
					it2 = CreateItem("syringe", x, y, z)
				Case "fine", "very fine"
					it2 = CreateItem("veryfinefirstaid", x, y, z)
			End Select
		Case "veryfinefirstaid"
			Select setting
				Case "rough"
					If Rand(2) = 1 Then
						it2 = CreateItem("firstaid2", x, y, z)
					Else
						it2 = CreateItem("firstaid", x, y, z)
					EndIf
				Case "coarse"
					If Rand(3) = 1 Then
						it2 = CreateItem("syringe", x, y, z)
					Else
						it2 = CreateItem("finefirstaid", x, y, z)
					EndIf
				Case "1:1", "fine", "very fine"
					it2 = CreateItem("veryfinesyringe", x, y, z)
			End Select
		Case "eyedrops", "redeyedrops"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1"
					If item\itemtemplate\namespec = "" Then
						it2 = CreateItem("eyedrops", x, y, z, "redeyedrops")
					Else
						it2 = CreateIteM("eyedrops", x, y, z)
					EndIf
				Case "fine"
					it2 = CreateItem("fineeyedrops", x,y,z)
				Case "very fine"
					it2 = CreateItem("supereyedrops", x,y,z)
			End Select
		Case "fineeyedrops"
			Select setting
				Case "rough"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.010, z, 90, Rand(360), 0)
					d\Size = 0.2 : EntityAlpha(d\obj, 0.8) : ScaleSprite(d\obj, d\Size, d\Size)
				Case "coarse"
					If Rand(3) = 1 Then
						it2 = CreateItem("eyedrops", x, y, z, "redeyedrops")
					Else
						it2 = CreateIteM("eyedrops", x, y, z)
					EndIf
				Case "1:1"
					remove = 0
				Case "fine", "very fine"
					it2 = CreateItem("supereyedrops", x,y,z)
			End Select
		Case "supereyedrops"
			Select setting
				Case "rough"
					If Rand(2) = 1 Then
						it2 = CreateItem("eyedrops", x, y, z, "redeyedrops")
					Else
						it2 = CreateIteM("eyedrops", x, y, z)
					EndIf
				Case "coarse"
					it2 = CreateItem("fineeyedrops", x,y,z)
				Case "1:1", "fine", "very fine"
					remove = 0
			End Select
		Case "syringe"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1"
					it2 = CreateItem("finefirstaid", x, y, z)	
				Case "fine"
					it2 = CreateItem("finesyringe", x, y, z)
				Case "very fine"
					it2 = CreateItem("veryfinesyringe", x, y, z)
			End Select
		Case "finesyringe"
			Select setting
				Case "rough"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
				Case "coarse"
					If Rand(3) = 1 Then
						it2 = CreateItem("firstaid", x, y, z)
					Else
						it2 = CreateItem("syringe", x, y, z)
					EndIf
				Case "1:1"
					it2 = CreateItem("firstaid2", x, y, z)	
				Case "fine", "very fine"
					it2 = CreateItem("veryfinesyringe", x, y, z)
			End Select
		Case "veryfinesyringe"
			Select setting
				Case "rough", "coarse", "1:1", "fine"
					it2 = CreateItem("misc", x, y, z, "electronical")	
				Case "very fine"
					n.NPCs = CreateNPC(NPCtype008,x,y,z)
					n\State = 2
			End Select
		Case "hand", "hand2"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(3, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1", "fine", "very fine"
					If (item\itemtemplate\tempname = "hand")
						it2 = CreateItem("hand2", x, y, z)
					Else
						it2 = CreateItem("hand", x, y, z)
					EndIf
			End Select
			;I believe a "fine" open-all hand would be too OP
		Case "key"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.1 : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1", "fine", "very fine"
					If Rand(10) Then
						it2 = CreateItem("scp860", x, y, z)
					Else
						remove = 0
					EndIf
			End Select
		Case "coin", "quarter"
			Select setting
				Case "rough", "coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.07 : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1"
					If item\itemtemplate\tempname = "coin" Then
						it2 = CreateItem("quarter", x, y, z)
					Else
						If Rand(50) = 1 Then
							it2 = CreateItem("coin", x, y, z)
						Else
							remove = 0
						EndIf
					EndIf
				Case "fine", "very fine"
					it2 = CreateItem("misc", x, y, z, "keymaster")
			End Select
		Case "clipboard", "wallet"
			Select setting
				Case "rough"
					d.Decals = CreateDecal(7, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
					ClearClipboard(item, 0)
				Case "coarse"
					If item\invSlots > 5 Then
						item\invSlots = item\invSlots - 5
						ClearClipboard(item, item\invSlots)
					ElseIf item\invSlots = 5
						item\invSlots = 1
						ClearClipboard(item, 1)
					Else ;1
						d.Decals = CreateDecal(7, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
						d\Size = 0.12 : ScaleSprite(d\obj, d\Size, d\Size)
						ClearClipboard(item, 0)
					EndIf
					remove = 0
				Case "1:1"
					remove = 0
				Case "fine"
					If item\invSlots = 1 Then
						item\invSlots = 5
					Else
						item\invSlots = Min(20, item\invSlots + 5)
					EndIf
					remove = 0
				Case "very fine"
					item\invSlots = 20
					remove = 0
			End Select
		Default
			Select setting
				Case "rough","coarse"
					d.Decals = CreateDecal(0, x, 8 * RoomScale + 0.005, z, 90, Rand(360), 0)
					d\Size = 0.2 : ScaleSprite(d\obj, d\Size, d\Size)
				Case "1:1","fine","very fine"
					remove = 0
			End Select
	End Select
	
	If remove Then
		RemoveItem(item)
	Else
		PositionEntity(item\collider, x, y, z)
		ResetEntity(item\collider)
	EndIf
	
	If it2 <> Null Then EntityType (it2\collider, HIT_ITEM)
End Function

Function ClearClipboard(item.Items, from%)
	For i% = from To 19
		If item\SecondInv[i]<>Null Then RemoveItem(item\SecondInv[i])
		item\SecondInv[i]=Null
	Next
End Function

Function GetPaper$(item.Items)
	If item <> Null Then
		If item\itemtemplate\namespec = "dblank" Lor item\State = 1 Lor Rand(3) = 1 Then
			Return "dblank"
		ElseIf Rand(100) = 1
			Return "burnt"
		EndIf
	EndIf
	Select Rand(25)
		Case 1
			Return "d008"
		Case 2
			Return "d012"
		Case 3
			Return "d035"
		Case 4
			Return "d049"
		Case 5
			Return "d079"
		Case 6
			Return "d096"
		Case 7
			Return "d106"
		Case 8
			Return "d173"
		Case 9
			Return "d205"
		Case 10
			Return "d372"
		Case 11
			Return "d427"
		Case 12
			Return "d500"
		Case 13
			Return "d513"
		Case 14
			Return "d682"
		Case 15
			Return "d714"
		Case 16
			Return "d860"
		Case 17
			Return "d8601"
		Case 18
			Return "d895"
		Case 19
			Return "d939"
		Case 20
			Return "d966"
		Case 21
			Return "d970"
		Case 22
			Return "d1048"
		Case 23
			Return "d1123"
		Case 24
			Return "d1162"
		Case 25
			Return "d1499"
	End Select
End Function