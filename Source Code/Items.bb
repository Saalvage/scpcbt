Global MaxItemAmount%
Global ItemAmount%
Dim Inventory.Items(0)
Global InvSelect%, SelectedItem.Items

Global ClosestItem.Items

Global LastItemID%

Type ItemTemplates
	Field namespec$ ;Additional name for things like documents
	Field tempname$
	Field localname$ ;Name that is to be drawn
	
	Field sound%
	
	Field found%
	
	Field obj%, objpath$, parentobjpath$
	Field invimg%,invimg2%,invimgpath$
	Field imgpath$, img%
	
	Field isAnim%
	
	Field scale#
	Field tex%, texpath$
End Type

Function CreateItemTemplate.ItemTemplates(tempname$, objpath$, invimgpath$, imgpath$, scale#, sound%, namespec$ = "", texturepath$ = "",invimgpath2$="",Anim%=0, texflags%=9)
	Local it.ItemTemplates = New ItemTemplates, n
	
	If namespec <> "" Then
		If Left(namespec, 1) = "!" Then
			namespec = Right(namespec, Len(namespec) - 1)
			it\localname = GetLocalStringR("Items", tempname, namespec)
			tempname = tempname + namespec
			namespec = ""
		Else
			it\localname = GetLocalString("Items", namespec)
		EndIf
	Else
		it\localname = GetLocalString("Items", tempname)
	EndIf
	
	;if another item shares the same object, copy it
	For it2.itemtemplates = Each ItemTemplates
		If it2\objpath = objpath And it2\obj <> 0 Then it\obj = CopyEntity(it2\obj) : it\parentobjpath=it2\objpath : Exit
	Next
	
	If it\obj = 0 Then; it\obj = LoadMesh(objpath)
		If Anim<>0 Then
			it\obj = LoadAnimMesh_Strict(objpath)
			it\isAnim=True
		Else
			it\obj = LoadMesh_Strict(objpath)
			it\isAnim=False
		EndIf
		it\objpath = objpath
	EndIf
	it\objpath = objpath
	
	Local texture%
	
	If texturepath <> "" Then
		For it2.itemtemplates = Each ItemTemplates
			If it2\texpath = texturepath And it2\tex<>0 Then
				texture = it2\tex
				Exit
			EndIf
		Next
		If texture=0 Then texture=LoadTexture_Strict(texturepath,texflags%) : it\texpath = texturepath; : DebugLog texturepath
		EntityTexture it\obj, texture
		it\tex = texture
	EndIf  
	
	it\scale = scale
	ScaleEntity it\obj, scale, scale, scale, True
	
	;if another item shares the same object, copy it
	For it2.itemtemplates = Each ItemTemplates
		If it2\invimgpath = invimgpath And it2\invimg <> 0 Then
			it\invimg = it2\invimg ;CopyImage()
			If it2\invimg2<>0 Then
				it\invimg2=it2\invimg2 ;CopyImage()
			EndIf
			Exit
		EndIf
	Next
	If it\invimg=0 Then
		it\invimg = LoadImage_Strict(invimgpath)
		it\invimgpath = invimgpath
		MaskImage(it\invimg, 255, 0, 255)
	EndIf
	
	If (invimgpath2 <> "") Then
		If it\invimg2=0 Then
			it\invimg2 = LoadImage_Strict(invimgpath2)
			MaskImage(it\invimg2,255,0,255)
		EndIf
	Else
		it\invimg2 = 0
	EndIf
	
	it\imgpath = imgpath
	
	it\tempname = tempname
	it\namespec = namespec
	
	it\sound = sound

	HideEntity it\obj
	
	Return it
	
End Function

Function InitItemTemplates()
	Local it.ItemTemplates,it2.ItemTemplates
	
	For i = 1 To 5
		CreateItemTemplate("key", "GFX\items\keycard.b3d", "GFX\items\INVkey" + i + ".jpg", "", 0.0004, 1, "!" + i, "GFX\items\keycard" + i + ".jpg")
	Next
	CreateItemTemplate("keyomni", "GFX\items\keycard.b3d", "GFX\items\INVkeyomni.jpg", "", 0.0004, 1, "", "GFX\items\keycardomni.jpg")
	CreateItemTemplate("misc", "GFX\items\keycard.b3d", "GFX\items\INVcard.jpg", "", 0.0004, 1, "keyplay", "GFX\items\card.jpg")
	CreateItemTemplate("misc", "GFX\items\keycard.b3d", "GFX\items\INVmastercard.jpg", "", 0.0004, 1, "keymaster", "GFX\items\mastercard.jpg")
	
	CreateItemTemplate("scp148", "GFX\items\metalpanel.b3d", "GFX\items\INVmetalpanel.jpg", "", RoomScale, 2)
	CreateItemTemplate("scp148ingot", "GFX\items\scp148.b3d", "GFX\items\INV148.jpg", "", RoomScale * 1.5, 2)
	CreateItemTemplate("scp178", "GFX\items\scp178.b3d", "GFX\items\INV178.jpg", "", 0.02, 1, "", "", "", 1)
							; state1: Refinement: (Coarse -2 (no blinking effect)), (Fine 1 (more SCP-178-1s, better blinking effect))
	CreateItemTemplate("scp420j", "GFX\items\420.b3d", "GFX\items\INV420.jpg", "", 0.0005, 2)
	CreateItemTemplate("cigarette", "GFX\items\420.b3d", "GFX\items\INV420.jpg", "", 0.0004, 2)
							; state1: Has the player tried to lit the cig?
	CreateItemTemplate("420s", "GFX\items\420.b3d", "GFX\items\INV420.jpg", "", 0.0004, 2, "joint")
	CreateItemTemplate("420s", "GFX\items\420.b3d", "GFX\items\INV420.jpg", "", 0.0004, 2, "smellyjoint")
	CreateItemTemplate("scp427", "GFX\items\427.b3d","GFX\items\INV427.jpg", "", 0.001, 3)
	CreateItemTemplate("super427", "GFX\items\427.b3d","GFX\items\INV427.jpg", "", 0.0011, 3)
	it = CreateItemTemplate("scp500", "GFX\items\pill.b3d", "GFX\items\INVpill.jpg", "", 0.0001, 2) : EntityColor it\obj,255,0,0
	it = CreateItemTemplate("upgradedpill", "GFX\items\pill.b3d", "GFX\items\INVpill.jpg", "", 0.0001, 2) : EntityColor it\obj,255,0,75
	it = CreateItemTemplate("pill", "GFX\items\pill.b3d", "GFX\items\INVpillwhite.jpg", "", 0.0001, 2) : EntityColor it\obj,255,255,255
	CreateItemTemplate("scp513", "GFX\items\513.b3d", "GFX\items\INV513.jpg", "", 0.1, 2)
							; state1: ID of the associated 513-1, if it has been spawned
	CreateItemTemplate("scp714", "GFX\items\scp714.b3d", "GFX\items\INV714.jpg", "", 0.3, 3)
	CreateItemTemplate("scp860", "GFX\items\key.b3d", "GFX\items\INVkey.jpg", "", 0.001, 3)
	CreateItemTemplate("scp1025", "GFX\items\scp1025.b3d", "GFX\items\INV1025.jpg", "", 0.1, 0)
							; state1: Next illness
							; state2: Refinement: (Coarse -1 (lower good chance)), (Fine 1 (higher good chance)), (Very Fine 2 (3x faster effect))
	CreateItemTemplate("book", "GFX\items\scp1025.b3d", "GFX\items\INVbook.jpg", "", 0.07, 0, "", "GFX\items\book_diff.png")
	CreateItemTemplate("scp1123", "GFX\items\HGIB_Skull1.b3d", "GFX\items\inv1123.jpg", "", 0.015, 2)
	CreateItemTemplate("bad1499","GFX\items\SCP-1499.b3d","GFX\items\INV1499.jpg", "", 0.022, 2)
	CreateItemTemplate("scp1499","GFX\items\SCP-1499.b3d","GFX\items\INV1499.jpg", "", 0.023, 2)
	CreateItemTemplate("super1499","GFX\items\SCP-1499.b3d","GFX\items\INV1499.jpg", "", 0.025, 2)
	CreateItemTemplate("fine1499","GFX\items\SCP-1499.b3d","GFX\items\INV1499.jpg", "", 0.024, 2)
							; state1: Puton progress (0 - 100)

	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc008.jpg", 0.003, 0, "d008")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc012.jpg", 0.003, 0, "d012")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc035.jpg", 0.003, 0, "d035")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc049.jpg", 0.003, 0, "d049")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc079.jpg", 0.003, 0, "d079")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc096.jpg", 0.003, 0, "d096")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc106.jpg", 0.003, 0, "d106")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc173.jpg", 0.003, 0, "d173")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc178.jpg", 0.003, 0, "d178")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc205.jpg", 0.003, 0, "d205")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc372.jpg", 0.003, 0, "d372")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc427.jpg", 0.003, 0, "d427")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc500.jpg", 0.003, 0, "d500")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc513.jpg", 0.003, 0, "d513")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc682.jpg", 0.003, 0, "d682")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc714.jpg", 0.003, 0, "d714")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc860.jpg", 0.003, 0, "d860")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc8601.jpg", 0.003, 0, "d8601")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc895.jpg", 0.003, 0, "d895")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc939.jpg", 0.003, 0, "d939")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc966.jpg", 0.003, 0, "d966")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc970.jpg", 0.003, 0, "d970")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc.cwm", 0.003, 0, "d990")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc1048.jpg", 0.003, 0, "d1048")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc1123.jpg", 0.003, 0, "d1123")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc1162.jpg", 0.003, 0, "d1162")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc1499.jpg", 0.003, 0, "d1499")
	
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc035ad.jpg", 0.003, 0, "035a")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc093rm.jpg", 0.003, 0, "093rm")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\docIR106.jpg", 0.003, 0, "ir1060204")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\docRP.jpg", 0.0025, 0, "rp106")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\docRAND2.jpg", 0.003, 0, "test")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc970odis.jpg", 0.003, 0, "970ds")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc1048a.jpg", 0.003, 0, "ir1048a")
	
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\docORI.jpg", 0.003, 0, "classd")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\docSC.jpg", 0.003, 0, "levels")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\docMTF.jpg", 0.003, 0, "mtf")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\docOBJC.jpg", 0.003, 0, "classes")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\docGOI.jpg", 0.003, 0, "gois")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\docRAND3.jpg", 0.003, 0, "document")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\docMSP.jpg", 0.003, 0, "modular")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\docmap.jpg", 0.003, 0, "sector02")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\docNDP.jpg", 0.003, 0, "nuclear")
	
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc106_2.jpg", 0.0025, 0, "allok")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVnote.jpg", "GFX\items\docL1.jpg", 0.0025, 0, "l1", "GFX\items\notetexture.jpg")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVnote.jpg", "GFX\items\docL2.jpg", 0.0025, 0, "l2", "GFX\items\notetexture.jpg")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVnote.jpg", "GFX\items\docL3.jpg", 0.0025, 0, "blood", "GFX\items\notetexture.jpg")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVbn.jpg", "GFX\items\docL4.jpg", 0.0025, 0, "lburnt1", "GFX\items\BurntNoteTexture.jpg")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVbn.jpg", "GFX\items\docL5.jpg", 0.0025, 0, "lburnt2", "GFX\items\BurntNoteTexture.jpg")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVbn.jpg", "GFX\items\docL6.jpg", 0.0025, 0, "scorched", "GFX\items\BurntNoteTexture.jpg")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVbn.jpg", "GFX\items\bn.it", 0.003, 0, "burnt", "GFX\items\BurntNoteTexture.jpg")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\docGonzales.jpg", 0.0025, 0, "journal")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\f4.jpg", 0.004, 0, "log1", "GFX\items\f4.jpg")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\f5.jpg", 0.004, 0, "log2", "GFX\items\f4.jpg")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\f6.jpg", 0.004, 0, "log3", "GFX\items\f4.jpg")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVnote.jpg", "GFX\items\docStrange.jpg", 0.0025, 0, "strange", "GFX\items\notetexture.jpg")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVnote.jpg", "GFX\items\sn.it", 0.003, 0, "mysterious", "GFX\items\notetexture.jpg")	
	CreateItemTemplate("paper", "GFX\items\note.b3d", "GFX\items\INVnote2.jpg", "GFX\items\docdan.jpg", 0.0025, 0, "daniel")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVnote.jpg", "GFX\items\docRAND1.jpg", 0.003, 0, "notification", "GFX\items\notetexture.jpg")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\dh.s", 0.003, 0, "hearing")
	CreateItemTemplate("paper", "GFX\items\note.b3d", "GFX\items\INVnote2.jpg", "GFX\items\note682.jpg", 0.0025, 0, "sticky")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\doc1048.jpg", 0.003, 0, "drawing")
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaper.jpg", "GFX\items\leaflet.jpg", 0.003, 0, "leaflet", "GFX\items\notetexture.jpg")
	
	CreateItemTemplate("paper", "GFX\items\paper.b3d", "GFX\items\INVpaperblank.jpg", "GFX\items\docBlank.jpg", 0.003, 0, "dblank", "GFX\items\paperblanktexture.jpg")
	
	CreateItemTemplate("misc", "GFX\items\origami.b3d", "GFX\items\INVorigami.jpg", "", 0.003, 0, "origami")
	
	CreateItemTemplate("misc", "GFX\items\electronics.b3d", "GFX\items\INVelectronics.jpg", "", 0.0015, 1, "electronical")
	
	CreateItemTemplate("badge", "GFX\items\badge.b3d", "GFX\items\INVbadge.jpg", "GFX\items\badge1.jpg", 0.0001, 1, "", "GFX\items\badge1_tex.jpg")
	
	CreateItemTemplate("badgasmask", "GFX\items\gasmask.b3d", "GFX\items\INVgasmask.jpg", "", 0.019, 2)
	CreateItemTemplate("gasmask", "GFX\items\gasmask.b3d", "GFX\items\INVgasmask.jpg", "", 0.02, 2)
	CreateItemTemplate("supergasmask", "GFX\items\gasmask.b3d", "GFX\items\INVgasmask.jpg", "", 0.021, 2)
	CreateItemTemplate("heavygasmask", "GFX\items\gasmask.b3d", "GFX\items\INVgasmask.jpg", "", 0.021, 2)
	
	CreateItemTemplate("hazmat0", "GFX\items\hazmat.b3d", "GFX\items\INVhazmat.jpg", "", 0.012, 2, "", "", "", 1)
	CreateItemTemplate("hazmat", "GFX\items\hazmat.b3d", "GFX\items\INVhazmat.jpg", "", 0.013, 2, "", "", "", 1)
	CreateItemTemplate("hazmat2", "GFX\items\hazmat.b3d", "GFX\items\INVhazmat.jpg", "", 0.014, 2, "", "", "", 1)
	CreateItemTemplate("hazmat3", "GFX\items\hazmat.b3d", "GFX\items\INVhazmat.jpg", "", 0.015, 2, "", "", "", 1)
						; state1: Puton progress (0 - 100)
	
	CreateItemTemplate("badnvg", "GFX\items\NVG.b3d", "GFX\items\INVnovision.jpg", "", 0.019, 2)
	CreateItemTemplate("nvg", "GFX\items\NVG.b3d", "GFX\items\INVnightvision.jpg", "", 0.02, 2)
	CreateItemTemplate("finenvg", "GFX\items\NVG.b3d", "GFX\items\INVveryfinenightvision.jpg", "", 0.021, 2)
	CreateItemTemplate("supernvg", "GFX\items\NVG.b3d", "GFX\items\INVsupernightvision.jpg", "", 0.022, 2)
						; state1: Battery level (0 - 1000)
						; state2: Used for interaction with SCP-895
						
	CreateItemTemplate("scramble", "GFX\items\SCRAMBLE.b3d", "GFX\items\INVscramble.jpg", "", 0.03, 2)
						; state1: Battery level (0 - 1000)
						; state2: Refinement: (Coarse -2 (sometimes malfunctions)), (1:1 +/-100 (white censor instead of black)) (Fine 1 (slower battery drain), (Very Fine 2 (way too big censor box)))
		
	CreateItemTemplate("badnav", "GFX\items\navigator.b3d", "GFX\items\INVnavigator.jpg", "GFX\items\navigator.png", 0.0008, 1)		
	CreateItemTemplate("nav300", "GFX\items\navigator.b3d", "GFX\items\INVnavigator.jpg", "GFX\items\navigator.png", 0.0008, 1)
	CreateItemTemplate("nav", "GFX\items\navigator.b3d", "GFX\items\INVnavigator.jpg", "GFX\items\navigator.png", 0.0008, 1)
	CreateItemTemplate("navulti", "GFX\items\navigator.b3d", "GFX\items\INVnavigator.jpg", "GFX\items\navigator.png", 0.0008, 1)
	CreateItemTemplate("nav310", "GFX\items\navigator.b3d", "GFX\items\INVnavigator.jpg", "GFX\items\navigator.png", 0.0008, 1)
						; state1: Battery level (0 - 100)
	
	CreateItemTemplate("radio", "GFX\items\radio.b3d", "GFX\items\INVradio.jpg", "GFX\items\radioHUD.png", 1.0, 1)
	CreateItemTemplate("fineradio", "GFX\items\radio.b3d", "GFX\items\INVradio.jpg", "GFX\items\radioHUD.png", 1.0, 1)
	CreateItemTemplate("veryfineradio", "GFX\items\radio.b3d", "GFX\items\INVradio.jpg", "GFX\items\radioHUD.png", 1.0, 1)
	CreateItemTemplate("18vradio", "GFX\items\radio.b3d", "GFX\items\INVradio.jpg", "GFX\items\radioHUD.png", 1.02, 1)
						; state1: Battery level (0 - 100)
						; state2: Selected track (0 - 5)
	
	CreateItemTemplate("bat", "GFX\items\Battery\Battery.b3d", "GFX\items\Battery\INVbattery9v.jpg", "", 0.008, 1)
	CreateItemTemplate("badbat", "GFX\items\Battery\Battery.b3d", "GFX\items\Battery\INVbattery4.5v.jpg", "", 0.008, 1, "", "GFX\items\Battery\Battery 4.5V.jpg")
	CreateItemTemplate("18vbat", "GFX\items\Battery\Battery.b3d", "GFX\items\Battery\INVbattery18v.jpg", "", 0.01, 1, "", "GFX\items\Battery\Battery 18V.jpg")
	CreateItemTemplate("killbat", "GFX\items\Battery\Battery.b3d", "GFX\items\Battery\INVbattery22900.jpg", "", 0.01, 1, "","GFX\items\Battery\Strange Battery.jpg")
	CreateItemTemplate("superbat", "GFX\items\Battery\Battery.b3d", "GFX\items\Battery\INVbattery999v.jpg", "", 0.009, 1, "", "GFX\items\Battery\Battery 999V.jpg")
	
	CreateItemTemplate("badvest", "GFX\items\vest.b3d", "GFX\items\INVvest.jpg", "", 0.018, 2)
	CreateItemTemplate("vest", "GFX\items\vest.b3d", "GFX\items\INVvest.jpg", "", 0.02, 2)
	CreateItemTemplate("finevest", "GFX\items\vest.b3d", "GFX\items\INVvest.jpg", "", 0.022, 2)
						; state1: Puton progress (0 - 100)
	
	CreateItemTemplate("veryfinevest", "GFX\items\vest.b3d", "GFX\items\INVvest.jpg", "", 0.025, 2)
	
	CreateItemTemplate("cup", "GFX\items\cup.b3d", "GFX\items\INVcup.jpg", "", 0.04, 2)
	CreateItemTemplate("emptycup", "GFX\items\cup.b3d", "GFX\items\INVcup.jpg", "", 0.04, 2)
	
	CreateItemTemplate("firstaid", "GFX\items\firstaid.b3d", "GFX\items\INVfirstaid.jpg", "", 0.05, 1)
	CreateItemTemplate("finefirstaid", "GFX\items\firstaid.b3d", "GFX\items\INVfirstaid.jpg", "", 0.03, 1)
	CreateItemTemplate("firstaid2", "GFX\items\firstaid.b3d", "GFX\items\INVfirstaid2.jpg", "", 0.03, 1, "", "GFX\items\firstaidkit2.jpg")
	CreateItemTemplate("veryfinefirstaid", "GFX\items\eyedrops.b3d", "GFX\items\INVbottle.jpg", "", 0.002, 1, "", "GFX\items\bottle.jpg")
						; state1: Apply progress (0 - 100)
	
	CreateItemTemplate("fineeyedrops", "GFX\items\eyedrops.b3d", "GFX\items\INVeyedrops.jpg", "", 0.0012, 1, "", "GFX\items\eyedrops.jpg")
	CreateItemTemplate("supereyedrops", "GFX\items\eyedrops.b3d", "GFX\items\INVeyedrops.jpg", "", 0.0012, 1, "", "GFX\items\eyedrops.jpg")
	CreateItemTemplate("eyedrops","GFX\items\eyedrops.b3d", "GFX\items\INVeyedrops.jpg", "", 0.0012, 1, "", "GFX\items\eyedrops.jpg")
	CreateItemTemplate("eyedrops", "GFX\items\eyedrops.b3d", "GFX\items\INVeyedropsred.jpg", "", 0.0012, 1, "redeyedrops", "GFX\items\eyedropsred.jpg")
	
	CreateItemTemplate("syringe", "GFX\items\Syringe\syringe.b3d", "GFX\items\Syringe\inv.png", "", 0.005, 2)
	CreateItemTemplate("finesyringe", "GFX\items\Syringe\syringe.b3d", "GFX\items\Syringe\inv.png", "", 0.005, 2)
	CreateItemTemplate("veryfinesyringe", "GFX\items\Syringe\syringe.b3d", "GFX\items\Syringe\inv.png", "", 0.005, 2)
	
	CreateItemTemplate("hand", "GFX\items\severedhand.b3d", "GFX\items\INVhand.jpg", "", 0.04, 2)
	CreateItemTemplate("hand2", "GFX\items\severedhand.b3d", "GFX\items\INVhand2.jpg", "", 0.04, 2, "", "GFX\items\shand2.png")
	CreateItemTemplate("hand3", "GFX\items\severedhand.b3d", "GFX\items\INVhand3.jpg", "", 0.04, 2, "", "GFX\items\shand3.png")
	
	CreateItemTemplate("key", "GFX\items\key.b3d", "GFX\items\INV1162_1.jpg", "", 0.001, 3, "lkey", "GFX\items\key2.png","",0,1+2+8)
	CreateItemTemplate("coin", "GFX\items\key.b3d", "GFX\items\INVcoinrust.jpg", "", 0.0005, 3, "", "GFX\items\coinrust.png","",0,1+2+8)
	CreateItemTemplate("ticket", "GFX\items\key.b3d", "GFX\items\INVticket.jpg", "GFX\items\ticket.png", 0.002, 0, "", "GFX\items\tickettexture.png","",0,1+2+8)
	CreateItemTemplate("badge", "GFX\items\badge.b3d", "GFX\items\INVoldbadge.jpg", "GFX\items\badge2.png", 0.0001, 1, "oldbadge", "GFX\items\badge2_tex.png","",0,1+2+8)
						; state1: If the nostalgia item has been viewed yet; if not, play the sound
	
	CreateItemTemplate("quarter", "GFX\items\key.b3d", "GFX\items\INVcoin.jpg", "", 0.0005, 3, "", "GFX\items\coin.png","",0,1+2+8)
	
	CreateItemTemplate("wallet", "GFX\items\wallet.b3d", "GFX\items\INVwallet.jpg", "", 0.0005, 2, "","","",1)
	CreateItemTemplate("clipboard", "GFX\items\clipboard.b3d", "GFX\items\INVclipboard.jpg", "", 0.003, 1, "", "", "GFX\items\INVclipboard2.jpg", 1)
	CreateItemTemplate("backpack", "GFX\items\backpack.b3d", "GFX\items\INVbackpack.jpg", "", 0.1, 2)
	
	For it = Each ItemTemplates
		If (it\tex<>0) Then
			If (it\texpath<>"") Then
				For it2=Each ItemTemplates
					If (it2<>it) And (it2\tex=it\tex) Then
						it2\tex = 0
					EndIf
				Next
			EndIf
			FreeTexture it\tex : it\tex = 0
		EndIf
	Next
	
End Function


Type Items
	Field localname$
	Field collider%,model%
	Field itemtemplate.ItemTemplates
	Field DropSpeed#
	
	Field r%,g%,b%,a#
	
	Field SoundChn%
	
	Field distsquared#, disttimer#
	
	Field state#, state2#
	
	Field Picked%,Dropped%
	
	Field invimg%
	Field WontColl% = False
	Field xspeed#,zspeed#
	Field SecondInv.Items[20]
	Field ID%
	Field invSlots%
End Type

Function CreateItem.Items(tempname$, x#, y#, z#, namespec$="", r%=0,g%=0,b%=0,a#=1.0,invSlots%=0)
	;CatchErrors("CreateItem")
	
	Local i.Items = New Items
	Local it.ItemTemplates
	
	tempname = Lower(tempname)
	namespec = Lower(namespec)
	
	For it.ItemTemplates = Each ItemTemplates
		If Lower(it\tempname) = tempname Then
			If Lower(it\namespec) = namespec Then
				i\itemtemplate = it
				i\collider = CreatePivot()
				EntityRadius i\collider, 0.01
				EntityPickMode i\collider, 1, False
				i\model = CopyEntity(it\obj,i\collider)
				i\localname = it\localname
				ShowEntity i\collider
				ShowEntity i\model
			EndIf
		EndIf
	Next
	
	i\WontColl = False
	
	If i\itemtemplate = Null Then RuntimeError("Item template not found ("+namespec+", "+tempname+")")
	
	ResetEntity i\collider		
	PositionEntity(i\collider, x, y, z, True)
	RotateEntity (i\collider, 0, Rand(360), 0)
	i\distsquared = EntityDistanceSquared(Collider, i\collider)
	i\DropSpeed = 0.0
	
	If tempname = "cup" Then
		i\r=r
		i\g=g
		i\b=b
		i\a=a
		
		Local liquid = CopyEntity(LiquidObj)
		ScaleEntity liquid, i\itemtemplate\scale,i\itemtemplate\scale,i\itemtemplate\scale,True
		PositionEntity liquid, EntityX(i\collider,True),EntityY(i\collider,True),EntityZ(i\collider,True)
		EntityParent liquid, i\model
		EntityColor liquid, r,g,b
		
		If a < 0 Then 
			EntityFX liquid, 1
			EntityAlpha liquid, Abs(a)
		Else
			EntityAlpha liquid, Abs(a)
		EndIf
		
		
		EntityShininess liquid, 1.0
	EndIf
	
	i\invimg = i\itemtemplate\invimg
	If (tempname="clipboard") And (invSlots=0) Then
		invSlots = 10
		SetAnimTime i\model,17.0
		i\invimg = i\itemtemplate\invimg2
	ElseIf (tempname="wallet") And (invSlots=0) Then
		invSlots = 10
		SetAnimTime i\model,0.0
	EndIf
	
	If tempname = "scp1025" Then
		If Rand(3-(state2<>2)*state2) = 1 Then ;higher chance for good illness if FINE, lower change for good illness if COARSE
			state = 6
		Else
			state = Rand(0,7)
		EndIf
	EndIf
	
	i\invSlots=invSlots
	
	i\ID=LastItemID+1
	LastItemID=i\ID
	
	;CatchErrors("Uncaught CreateItem")
	Return i
End Function

Function RemoveItem(i.Items)
	;CatchErrors("RemoveItem")
	Local n
	FreeEntity(i\model) : FreeEntity(i\collider) : i\collider = 0
	
	For n% = 0 To MaxItemAmount - 1
		If Inventory(n) = i
			Inventory(n) = Null
			ItemAmount = ItemAmount-1
			Exit
		EndIf
	Next
	If SelectedItem = i Then
		Select SelectedItem\itemtemplate\tempname
			Case "badnvg", "nvg", "finenvg", "supernvg"
				WearingNightVision = False
			Case "scramble"
				WearingScramble = False
			Case "badgasmask", "gasmask", "supergasmask", "heavygasmask"
				WearingGasMask = False
			Case "badvest", "vest", "finevest"
				WearingVest = False
			Case "hazmat0","hazmat","hazmat2","hazmat3"
				WearingHazmat = False
			Case "scp714"
				Wearing714 = False
			Case "bad1499","scp1499","super1499","fine1499"
				Wearing1499 = False
			Case "scp427", "super427"
				Local I_427.SCP427 = First SCP427
				I_427\Using = 0
		End Select
		
		SelectedItem = Null
	EndIf
	If i\itemtemplate\img <> 0
		FreeImage i\itemtemplate\img
		i\itemtemplate\img = 0
	EndIf
	Delete i
	
	;CatchErrors("Uncaught RemoveItem")
End Function


Function UpdateItems()
	;CatchErrors("UpdateItems")
	Local n, i.Items, i2.Items
	Local xtemp#, ytemp#, ztemp#
	Local temp%, np.NPCs
	Local pick%
	
	Local deletedItem% = False
	
	ClosestItem = Null
	For i.Items = Each Items
		i\Dropped = 0
		
		If (i\Picked = 0) Then
			Local HideDist = PowTwo(HideDistance*0.5)
			If i\disttimer < MilliSecs() Then
				i\distsquared = EntityDistanceSquared(Camera, i\collider)
				i\disttimer = MilliSecs() + 700
				If i\distsquared < HideDist Then ShowEntity i\collider
			EndIf
			
			If i\distsquared < HideDist Then
				ShowEntity i\collider
				
				If i\distsquared < PowTwo(1.2) Then
					If ClosestItem = Null Then
						If EntityInView(i\model, Camera) Then
							If EntityVisible(i\collider,Camera) Then
								ClosestItem = i
							EndIf
						EndIf
					ElseIf ClosestItem = i Lor i\distsquared < EntityDistanceSquared(Camera, ClosestItem\collider) Then
						If EntityInView(i\model, Camera) Then
							If EntityVisible(i\collider,Camera) Then
								ClosestItem = i
							EndIf
						EndIf
					EndIf
				EndIf
				
				Local HideDistPointTwo = PowTwo(HideDistance*0.5*0.2)
				If i\distsquared<HideDistPointTwo Then
					For i2.Items = Each Items
						If i<>i2 And (i2\Picked = 0) And i2\distsquared<HideDistPointTwo Then
							
							xtemp# = (EntityX(i2\collider,True)-EntityX(i\collider,True))
							ytemp# = (EntityY(i2\collider,True)-EntityY(i\collider,True))
							ztemp# = (EntityZ(i2\collider,True)-EntityZ(i\collider,True))
							
							ed# = (xtemp*xtemp+ztemp*ztemp)
							
							If ed<0.07 And Abs(ytemp)<0.25 Then
								;items are too close together, push away
								xtemp = xtemp*(0.07-ed)
								ztemp = ztemp*(0.07-ed)
								
								While Abs(xtemp)+Abs(ztemp)<0.001
									xtemp = xtemp+Rnd(-0.002,0.002)
									ztemp = ztemp+Rnd(-0.002,0.002)
								Wend
								
								TranslateEntity i2\collider,xtemp,0,ztemp
								TranslateEntity i\collider,-xtemp,0,-ztemp
							EndIf
						EndIf
					Next
				EndIf
				
				If EntityCollided(i\collider, HIT_MAP) Then
					i\DropSpeed = 0
					i\xspeed = 0.0
					i\zspeed = 0.0
				Else
					If ShouldEntitiesFall
						pick = LinePick(EntityX(i\collider),EntityY(i\collider),EntityZ(i\collider),0,-10,0)
						If pick
							i\DropSpeed = i\DropSpeed - 0.0004 * FPSfactor
							TranslateEntity i\collider, i\xspeed*FPSfactor, i\DropSpeed * FPSfactor, i\zspeed*FPSfactor
							If i\WontColl Then ResetEntity(i\collider)
						Else
							i\DropSpeed = 0
							i\xspeed = 0.0
							i\zspeed = 0.0
						EndIf
					Else
						i\DropSpeed = 0
						i\xspeed = 0.0
						i\zspeed = 0.0
					EndIf
				EndIf
				
				If EntityY(i\collider) < - 35.0 Then DebugLog "remove: " + i\itemtemplate\localname:RemoveItem(i):deletedItem=True
			Else
				HideEntity i\collider
			EndIf
		Else
			i\DropSpeed = 0
			i\xspeed = 0.0
			i\zspeed = 0.0
		EndIf
		
		If Not deletedItem Then
			;CatchErrors("Uncaught "+Chr(34)+i\itemtemplate\localname+Chr(34)+" item")
		EndIf
		deletedItem = False
	Next
	
	If ClosestItem <> Null Then
		;DrawHandIcon = True
		
		If MouseHit1 Then PickItem(ClosestItem)
	EndIf
	
End Function

Function PickItem(item.Items)
	Local n% = 0
	Local canpickitem = True
	Local fullINV% = True
	
	For n% = 0 To MaxItemAmount - 1
		If Inventory(n)=Null
			fullINV = False
			Exit
		EndIf
	Next
	
	If WearingHazmat <> 0 Then
		If Instr(item\itemtemplate\tempname,"hazmat") Then
			Msg = GetLocalString("Messages", "hazmatdouble")
		Else
			Msg = GetLocalString("Messages", "cantpickhazmat")
		EndIf
		MsgTimer = 70*5
		Return
	EndIf
	
	;CatchErrors("PickItem")
	If (Not fullINV) Then
		For n% = 0 To MaxItemAmount - 1
			If Inventory(n) = Null Then
				Select item\itemtemplate\tempname
					Case "scp178"
						SetAnimTime(item\model, 2)
					Case "scp1123"
						If Not (Wearing714 = 1) Then
							If PlayerRoom\RoomTemplate\Name <> "room1123" Then
								ShowEntity Light
								LightFlash = 7
								PlaySound_Strict(LoadTempSound("SFX\SCP\1123\Touch.ogg"))		
								DeathMSG = GetLocalString("Deaths", "1123")
								Kill()
							EndIf
							For e.Events = Each Events
								If e\eventname = "room1123" Then 
									If e\eventstate = 0 Then
										ShowEntity Light
										LightFlash = 3
										PlaySound_Strict(LoadTempSound("SFX\SCP\1123\Touch.ogg"))
									EndIf
									e\eventstate = Max(1, e\eventstate)
									
									Exit
								EndIf
							Next
							Return
						EndIf
					Case "killbat"
						ShowEntity Light
						LightFlash = 1.0
						PlaySound_Strict(IntroSFX(11))
						DeathMSG = GetLocalString("Deaths", "killbat")
						Kill()
					Case "scp148"
						GiveAchievement(Achv148)
					Case "scp513"
						GiveAchievement(Achv513)
					Case "scp860"
						GiveAchievement(Achv860)
					Case "keyomni"
						GiveAchievement(AchvOmni)
					Case "veryfinevest"
						Msg = GetLocalString("Messages", "cantpickvestheavy")
						MsgTimer = 70*6
						Exit
					Case "firstaid", "finefirstaid", "veryfinefirstaid", "firstaid2"
						item\state = 0
					Case "navulti"
						GiveAchievement(AchvSNAV)
					Case "fineradio", "veryfineradio"
						item\state = 101
					Case "hazmat0", "hazmat", "hazmat2", "hazmat3"
						SetAnimTime(item\model, 4)
						canpickitem = True
						For z% = 0 To MaxItemAmount - 1
							If Inventory(z) <> Null Then
								If Inventory(z)\itemtemplate\tempname="hazmat0" Lor Inventory(z)\itemtemplate\tempname="hazmat" Lor Inventory(z)\itemtemplate\tempname="hazmat2" Lor Inventory(z)\itemtemplate\tempname="hazmat3" Then
									canpickitem% = False
									Exit
								ElseIf Inventory(z)\itemtemplate\tempname="badvest" Lor Inventory(z)\itemtemplate\tempname="vest" Lor Inventory(z)\itemtemplate\tempname="finevest" Then
									canpickitem% = 2
									Exit
								EndIf
							EndIf
						Next
						
						If canpickitem=False Then
							Msg = GetLocalString("Messages", "hazmatdouble")
							MsgTimer = 70 * 5
							Return
						ElseIf canpickitem=2 Then
							Msg = GetLocalString("Messages", "cantvesthaz")
							MsgTimer = 70 * 5
							Return
						Else
							;TakeOffStuff(1+16)
							SelectedItem = item
						EndIf
					Case "badvest","vest","finevest"
						canpickitem = True
						For z% = 0 To MaxItemAmount - 1
							If Inventory(z) <> Null Then
								If Inventory(z)\itemtemplate\tempname="badvest" Lor Inventory(z)\itemtemplate\tempname="vest" Lor Inventory(z)\itemtemplate\tempname="finevest" Then
									canpickitem% = False
									Exit
								ElseIf Inventory(z)\itemtemplate\tempname="hazmat0" Lor Inventory(z)\itemtemplate\tempname="hazmat" Lor Inventory(z)\itemtemplate\tempname="hazmat2" Lor Inventory(z)\itemtemplate\tempname="hazmat3" Then
									canpickitem% = 2
									Exit
								EndIf
							EndIf
						Next
						
						If canpickitem=False Then
							Msg = GetLocalString("Messages", "vestdouble")
							MsgTimer = 70 * 5
							Return
						ElseIf canpickitem=2 Then
							Msg = GetLocalString("Messages", "cantvesthaz")
							MsgTimer = 70 * 5
							Return
						Else
							;TakeOffStuff(2)
							SelectedItem = item
						EndIf
					Case "cup"
						If item\state2 = 0 Then
							item\state = 1
							item\state2 = 1
						EndIf
				End Select
				
				If item\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(item\itemtemplate\sound))
				item\Picked = 1
				item\Dropped = -1
				
				item\itemtemplate\found=True
				ItemAmount = ItemAmount + 1
				
				Inventory(n) = item
				HideEntity(item\collider)
				Exit
			EndIf
		Next
	Else
		Msg = GetLocalString("Messages", "cantcarrymore")
		MsgTimer = 70 * 5
	EndIf
	;CatchErrors("Uncaught PickItem")
End Function

Function DropItem(item.Items,playdropsound%=True)
	If WearingHazmat <> 0 Then
		Msg = GetLocalString("Messages", "cantdrophaz")
		MsgTimer = 70*5
		Return
	EndIf
	
	;CatchErrors("DropItem")
	If playdropsound Then
		If item\itemtemplate\sound <> 66 Then PlaySound_Strict(PickSFX(item\itemtemplate\sound))
	EndIf
	
	item\Dropped = 1
	
	ShowEntity(item\collider)
	PositionEntity(item\collider, EntityX(Camera), EntityY(Camera), EntityZ(Camera))
	RotateEntity(item\collider, EntityPitch(Camera), EntityYaw(Camera)+Rnd(-20,20), 0)
	MoveEntity(item\collider, 0, -0.1, 0.1)
	RotateEntity(item\collider, 0, EntityYaw(Camera)+Rnd(-110,110), 0)
	
	ResetEntity (item\collider)
	
	For z% = 0 To MaxItemAmount - 1
		If Inventory(z) = item Then Inventory(z) = Null
	Next
	If item\Picked = 2 Then
		Select item\itemtemplate\tempname
			Case "badgasmask", "gasmask", "supergasmask", "heavygasmask"
				WearingGasMask = False
			Case "hazmat0", "hazmat", "hazmat2", "hazmat3"
				WearingHazmat = False
			Case "badvest", "vest", "finevest"
				WearingVest = False
			Case "badnvg"
				If WearingNightVision = -1 Then WearingNightVision = False
			Case "nvg"
				If WearingNightVision = 1 Then CameraFogFar = StoredCameraFogFar : WearingNightVision = False
			Case "supernvg"
				If WearingNightVision = 2 Then CameraFogFar = StoredCameraFogFar : WearingNightVision = False
			Case "finenvg"
				If WearingNightVision = 3 Then CameraFogFar = StoredCameraFogFar : WearingNightVision = False
			Case "scramble"
				WearingScramble = False
			Case "scp178"
				Wearing178 = 0
			Case "scp427", "super427"
				Local I_427.SCP427 = First SCP427
				I_427\Using = 0
			Case "scp714"
				Wearing714 = False
			Case "bad1499","scp1499","super1499","fine1499"
				Wearing1499 = False
		End Select
	EndIf
	
	item\Picked = 0
	
	;CatchErrors("Uncaught DropItem")
	
End Function

;Update any ailments inflicted by SCP-294 drinks.
Function Update294()
	;CatchErrors("Update294")
	
	If CameraShakeTimer > 0 Then
		CameraShakeTimer = CameraShakeTimer - (FPSfactor/70)
		CameraShake = 2
	EndIf
	
	If VomitTimer > 0 Then
		DebugLog VomitTimer
		VomitTimer = VomitTimer - (FPSfactor/70)
		
		If (MilliSecs() Mod 1600) < Rand(200, 400) Then
			If BlurTimer = 0 Then BlurTimer = Rnd(10, 20)*70
			CameraShake = Rnd(0, 2)
		EndIf
		
;		If (MilliSecs() Mod 1000) < Rand(1200) Then 
		
		If Rand(50) = 50 And (MilliSecs() Mod 4000) < 200 Then PlaySound_Strict(CoughSFX(Rand(0,2)))
		
		;Regurgitate when timer is below 10 seconds. (ew)
		If VomitTimer < 10 And Rnd(0, 500 * VomitTimer) < 2 Then
			If (Not ChannelPlaying(VomitCHN)) And (Not Regurgitate) Then
				VomitCHN = PlaySound_Strict(LoadTempSound("SFX\SCP\294\Retch" + Rand(1, 2) + ".ogg"))
				Regurgitate = MilliSecs() + 50
			EndIf
		EndIf
		
		If Regurgitate > MilliSecs() And Regurgitate <> 0 Then
			mouse_y_speed_1 = mouse_y_speed_1 + 1.0
		Else
			Regurgitate = 0
		EndIf
		
	ElseIf VomitTimer < 0 Then ;vomit
		VomitTimer = VomitTimer - (FPSfactor/70)
		
		If VomitTimer > -5 Then
			If (MilliSecs() Mod 400) < 50 Then CameraShake = 4 
			mouse_x_speed_1 = 0.0
			Playable = False
		Else
			Playable = True
		EndIf
		
		If (Not Vomit) Then
			BlurTimer = 40 * 70
			VomitSFX = LoadSound_Strict("SFX\SCP\294\Vomit.ogg")
			VomitCHN = PlaySound_Strict(VomitSFX)
			PrevInjuries = Injuries
			PrevBloodloss = Bloodloss
			Injuries = 1.5
			Bloodloss = 70
			EyeIrritation = 9 * 70
			
			pvt = CreatePivot()
			PositionEntity(pvt, EntityX(Camera), EntityY(Collider) - 0.05, EntityZ(Camera))
			TurnEntity(pvt, 90, 0, 0)
			EntityPick(pvt, 0.3)
			de.decals = CreateDecal(5, PickedX(), PickedY() + 0.005, PickedZ(), 90, 180, 0)
			de\Size = 0.001 : de\SizeChange = 0.001 : de\MaxSize = 0.6 : EntityAlpha(de\obj, 1.0) : EntityColor(de\obj, 0.0, Rnd(200, 255), 0.0) : ScaleSprite de\obj, de\size, de\size
			FreeEntity pvt
			Vomit = True
		EndIf
		
		UpdateDecals()
		
		mouse_y_speed_1 = mouse_y_speed_1 + Max((1.0 + VomitTimer / 10), 0.0)
		
		If VomitTimer < -15 Then
			FreeSound_Strict(VomitSFX)
			VomitTimer = 0
			If KillTimer >= 0 Then
				PlaySound_Strict(BreathSFX(0,0))
			EndIf
			Injuries = PrevInjuries
			Bloodloss = PrevBloodloss
			Vomit = False
		EndIf
	EndIf
	
	;CatchErrors("Uncaught Update294")
End Function