Type Difficulty
	Field name$
	Field description$
	Field permaDeath%
	Field aggressiveNPCs
	Field saveType%
	Field items%
	Field otherFactors%
	
	Field r%
	Field g%
	Field b%
	
	Field customizable%
End Type

Global difficulties.Difficulty[5]

Global SelectedDifficulty.Difficulty

Global ApolUnlocked = False
If FileType("does the black moon howl") = 1 Then
	dtbmh$ = OpenFile("does the black moon howl")
	If ReadString(dtbmh) = "when the foundation crumbles" Then ApolUnlocked = True
	CloseFile(dtbmh)
EndIf

Const SAFE=0, EUCLID=1, KETER=2, APOLLYON=3, CUSTOM=4

Const SAVEANYWHERE = 0, SAVEONQUIT=1, SAVEONSCREENS=2

Const EASY = 0, NORMAL = 1, HARD = 2, EXTREME = 3

difficulties[SAFE] = New Difficulty
difficulties[SAFE]\name = GetLocalString("Menu", "safe")
difficulties[SAFE]\description =GetLocalString("Menu", "safe_desc")
difficulties[SAFE]\permaDeath = False
difficulties[SAFE]\aggressiveNPCs = False
difficulties[SAFE]\saveType = SAVEANYWHERE
difficulties[SAFE]\items = 12
difficulties[SAFE]\otherFactors = EASY
difficulties[SAFE]\r = 120
difficulties[SAFE]\g = 150
difficulties[SAFE]\b = 50

difficulties[EUCLID] = New Difficulty
difficulties[EUCLID]\name = GetLocalString("Menu", "euclid")
difficulties[EUCLID]\description = GetLocalString("Menu", "euclid_desc")
difficulties[EUCLID]\permaDeath = False
difficulties[EUCLID]\aggressiveNPCs = False
difficulties[EUCLID]\saveType = SAVEONSCREENS
difficulties[EUCLID]\items = 10
difficulties[EUCLID]\otherFactors = NORMAL
difficulties[EUCLID]\r = 200
difficulties[EUCLID]\g = 200
difficulties[EUCLID]\b = 0

difficulties[KETER] = New Difficulty
difficulties[KETER]\name = GetLocalString("Menu", "keter")
difficulties[KETER]\description = GetLocalString("Menu", "keter_desc")
difficulties[KETER]\permaDeath = True
difficulties[KETER]\aggressiveNPCs = True
difficulties[KETER]\saveType = SAVEONQUIT
difficulties[KETER]\items = 8
difficulties[KETER]\otherFactors = HARD
difficulties[KETER]\r = 200
difficulties[KETER]\g = 0
difficulties[KETER]\b = 0

difficulties[APOLLYON] = New Difficulty
difficulties[APOLLYON]\name = GetLocalString("Menu", "apollyon")
difficulties[APOLLYON]\description = GetLocalString("Menu", "apollyon_desc")
difficulties[APOLLYON]\permaDeath = True
difficulties[APOLLYON]\aggressiveNPCs = True
difficulties[APOLLYON]\saveType = SAVEONQUIT
difficulties[APOLLYON]\items = 2
difficulties[APOLLYON]\otherFactors = EXTREME
If ApolUnlocked Then
	difficulties[APOLLYON]\r = 111
	difficulties[APOLLYON]\g = 0
	difficulties[APOLLYON]\b = 0
Else
	difficulties[APOLLYON]\r = 50
	difficulties[APOLLYON]\g = 50
	difficulties[APOLLYON]\b = 50
EndIf

difficulties[CUSTOM] = New Difficulty
difficulties[CUSTOM]\name = GetLocalString("Menu", "custom")
difficulties[CUSTOM]\permaDeath = False
difficulties[CUSTOM]\aggressiveNPCs = True
difficulties[CUSTOM]\saveType = SAVEANYWHERE
difficulties[CUSTOM]\customizable = True
difficulties[CUSTOM]\items = 20
difficulties[CUSTOM]\otherFactors = EASY
difficulties[CUSTOM]\r = 255
difficulties[CUSTOM]\g = 255
difficulties[CUSTOM]\b = 255

SelectedDifficulty = difficulties[SAFE]