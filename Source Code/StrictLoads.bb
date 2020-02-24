; ID: 2975
; Author: RifRaf, further modified by MonocleBios, even further modified by Salvage
; Date: 2012-09-11 11:44:22
; Title: Safe Loads (b3d) ;strict loads sounds more appropriate IMO
; Description: Get the missing filename reported

;safe loads for mav trapping media issues




;basic wrapper functions that check to make sure that the file exists before attempting to load it, raises an RTE if it doesn't
;more informative alternative to MAVs outside of debug mode, makes it immiediately obvious whether or not someone is loading resources
;likely to cause more crashes than 'clean' CB, as this prevents anyone from loading any assets that don't exist, regardless if they are ever used
;added zero checks since blitz load functions return zero sometimes even if the filetype exists
Function LoadImage_Strict(file$)
	Local I_Loc.Loc = First Loc
	
	If I_Loc\Localized And FileType(I_Loc\LangPath + file$)=1 Then
		Return LoadImage(I_Loc\LangPath + file$)
	EndIf
	If FileType(file$)<>1 Then RuntimeError "Image " + Chr(34) + file$ + Chr(34) + " missing. "
	tmp = LoadImage(file$)
	Return tmp
	;attempt to load the image again
	If tmp = 0 Then tmp2 = LoadImage(file)
	DebugLog "Attempting to load again: "+file
	Return tmp2
End Function


Type Sound
	Field internalHandle%
	Field name$
	Field channels%[32]
	Field releaseTime%
End Type

Function AutoReleaseSounds()
	Local snd.Sound
	For snd.Sound = Each Sound
		Local tryRelease% = True
		For i = 0 To 31
			If snd\channels[i] <> 0 Then
				If ChannelPlaying(snd\channels[i]) Then
					tryRelease = False
					snd\releaseTime = MilliSecs()+5000
					Exit
				EndIf
			EndIf
		Next
		If tryRelease Then
			If snd\releaseTime < MilliSecs() Then
				If snd\internalHandle <> 0 Then
					FreeSound snd\internalHandle
					snd\internalHandle = 0
				EndIf
			EndIf
		EndIf
	Next
End Function

Function PlaySound_Strict%(sndHandle%)
	Local snd.Sound = Object.Sound(sndHandle)
	If snd <> Null Then
		Local shouldPlay% = True
		For i = 0 To 31
			If snd\channels[i] <> 0 Then
				If Not ChannelPlaying(snd\channels[i]) Then
					If snd\internalHandle = 0 Then
						If FileType(snd\name) <> 1 Then
							CreateConsoleMsg("Sound " + Chr(34) + snd\name + Chr(34) + " not found.")
							If ConsoleOpening
								ConsoleOpen = True
							EndIf
						Else
							If EnableSFXRelease Then snd\internalHandle = LoadSound(snd\name)
						EndIf
						If snd\internalHandle = 0 Then
							CreateConsoleMsg("Failed to load Sound: " + Chr(34) + snd\name + Chr(34))
							If ConsoleOpening
								ConsoleOpen = True
							EndIf
						EndIf
					EndIf
					If ConsoleFlush Then
						snd\channels[i] = PlaySound(ConsoleFlushSnd)
					Else
						snd\channels[i] = PlaySound(snd\internalHandle)
					EndIf
					ChannelVolume snd\channels[i],SFXVolume#
					snd\releaseTime = MilliSecs()+5000 ;release after 5 seconds
					Return snd\channels[i]
				EndIf
			Else
				If snd\internalHandle = 0 Then
					If FileType(snd\name) <> 1 Then
						CreateConsoleMsg("Sound " + Chr(34) + snd\name + Chr(34) + " not found.")
						If ConsoleOpening
							ConsoleOpen = True
						EndIf
					Else
						If EnableSFXRelease Then snd\internalHandle = LoadSound(snd\name)
					EndIf
						
					If snd\internalHandle = 0 Then
						CreateConsoleMsg("Failed to load Sound: " + Chr(34) + snd\name + Chr(34))
						If ConsoleOpening
							ConsoleOpen = True
						EndIf
					EndIf
				EndIf
				If ConsoleFlushSnd Then
					snd\channels[i] = PlaySound(ConsoleFlushSnd)
				Else
					snd\channels[i] = PlaySound(snd\internalHandle)
				EndIf
				ChannelVolume snd\channels[i],SFXVolume#
				snd\releaseTime = MilliSecs()+5000 ;release after 5 seconds
				Return snd\channels[i]
			EndIf
		Next
	EndIf
	
	Return 0
End Function

Function LoadSound_Strict(file$)
	Local I_Loc.Loc = First Loc
	
	If I_Loc\Localized And FileType(I_Loc\LangPath + file$)=1 Then
		file = I_Loc\LangPath + file
	EndIf
	Local snd.Sound = New Sound
	snd\name = file
	snd\internalHandle = 0
	snd\releaseTime = 0
	If (Not EnableSFXRelease) Then
		If snd\internalHandle = 0 Then 
			snd\internalHandle = LoadSound(snd\name)
		EndIf
	EndIf
	
	Return Handle(snd)
End Function

Function FreeSound_Strict(sndHandle%)
	Local snd.Sound = Object.Sound(sndHandle)
	If snd <> Null Then
		If snd\internalHandle <> 0 Then
			FreeSound snd\internalHandle
			snd\internalHandle = 0
		EndIf
		Delete snd
	EndIf
End Function

Type Stream
	Field chn%
End Type

Function StreamSound_Strict(file$,volume#=1.0,custommode=2)
	Local I_Loc.Loc = First Loc
	
	If I_Loc\Localized And FileType(I_Loc\LangPath + file$)=1 Then
		file = I_Loc\LangPath + file
	EndIf
	
	If FileType(file$)<>1
		CreateConsoleMsg("Sound " + Chr(34) + file$ + Chr(34) + " not found.")
		If ConsoleOpening
			ConsoleOpen = True
		EndIf
		Return 0
	EndIf
	
	Local st.Stream = New Stream
	
	st\chn = PlayMusic(file$,custommode)
	
	If st\chn = -1
		CreateConsoleMsg("Failed to stream Sound (returned -1): " + Chr(34) + file$ + Chr(34))
		If ConsoleOpening
			ConsoleOpen = True
		EndIf
		Return -1
	EndIf
	ChannelVolume(st\chn,volume*1.0)
	Return Handle(st)
End Function

Function StopStream_Strict(streamHandle%)
	Local st.Stream = Object.Stream(streamHandle)
	
	If st = Null
		CreateConsoleMsg("Failed to stop stream Sound: Unknown Stream")
		Return
	EndIf
	If st\chn=0 Lor st\chn=-1
		CreateConsoleMsg("Failed to stop stream Sound: Return value "+st\chn)
		Return
	EndIf
	StopChannel(st\chn)
	Delete st
End Function

Function SetStreamVolume_Strict(streamHandle%,volume#)
	Local st.Stream = Object.Stream(streamHandle)
	
	If st = Null
		CreateConsoleMsg("Failed to set stream Sound volume: Unknown Stream")
		Return
	EndIf
	If st\chn=0 Lor st\chn=-1
		CreateConsoleMsg("Failed to set stream Sound volume: Return value "+st\chn)
		Return
	EndIf
	
	ChannelVolume(st\chn,volume)
	
End Function

Function SetStreamPaused_Strict(streamHandle%,paused%)
	Local st.Stream = Object.Stream(streamHandle)
	
	If st = Null
		CreateConsoleMsg("Failed to pause/unpause stream Sound: Unknown Stream")
		Return
	EndIf
	If st\chn=0 Lor st\chn=-1
		CreateConsoleMsg("Failed to pause/unpause stream Sound: Return value "+st\chn)
		Return
	EndIf
	
	If paused Then
		PauseChannel(st\chn)
	Else
		ResumeChannel(st\chn)
	EndIf
	
End Function

Function IsStreamPlaying_Strict(streamHandle%)
	Local st.Stream = Object.Stream(streamHandle)
	
	If st = Null
		CreateConsoleMsg("Failed to find stream Sound: Unknown Stream")
		Return
	EndIf
	If st\chn=0 Lor st\chn=-1
		CreateConsoleMsg("Failed to find stream Sound: Return value "+st\chn)
		Return
	EndIf
	
	Return ChannelPlaying(st\chn)
	
End Function

Function SetStreamPan_Strict(streamHandle%,pan#)
	Local st.Stream = Object.Stream(streamHandle)
	
	If st = Null
		CreateConsoleMsg("Failed to find stream Sound: Unknown Stream")
		Return
	EndIf
	If st\chn=0 Lor st\chn=-1
		CreateConsoleMsg("Failed to find stream Sound: Return value "+st\chn)
		Return
	EndIf
	
	;-1 = Left
	;0 = Middle
	;1 = Right
	ChannelPan(st\chn,pan)
	
End Function

Function UpdateStreamSoundOrigin(streamHandle%,cam%,entity%,range#=10,volume#=1.0)
	;Local st.Stream = Object.Stream(streamHandle)
	range# = Max(range,1.0)
	
	If volume>0 Then
		
		Local dist# = EntityDistance(cam, entity) / range#
		If 1 - dist# > 0 And 1 - dist# < 1 Then
			
			Local panvalue# = Sin(-DeltaYaw(cam,entity))
			
			SetStreamVolume_Strict(streamHandle,volume#*(1-dist#)*SFXVolume#)
			SetStreamPan_Strict(streamHandle,panvalue)
		Else
			SetStreamVolume_Strict(streamHandle,0.0)
		EndIf
	Else
		If streamHandle <> 0 Then
			SetStreamVolume_Strict(streamHandle,0.0)
		EndIf 
	EndIf
End Function

Function LoadMesh_Strict(File$,parent=0)
	Local I_Loc.Loc = First Loc
	
	If I_Loc\Localized And FileType(I_Loc\LangPath + file$)=1 Then
		Return LoadMesh(I_Loc\LangPath + File$, parent)
	EndIf
	If FileType(File$) <> 1 Then RuntimeError "3D Mesh " + File$ + " not found."
	tmp = LoadMesh(File$, parent)
	If tmp = 0 Then RuntimeError "Failed to load 3D Mesh: " + File$ 
	Return tmp  
End Function   

Function LoadAnimMesh_Strict(File$,parent=0)
	Local I_Loc.Loc = First Loc
	
	If I_Loc\Localized And FileType(I_Loc\LangPath + file$)=1 Then
		Return LoadAnimMesh(I_Loc\LangPath + File$, parent)
	EndIf
	If FileType(File$) <> 1 Then RuntimeError "3D Animated Mesh " + File$ + " not found."
	tmp = LoadAnimMesh(File$, parent)
	If tmp = 0 Then RuntimeError "Failed to load 3D Animated Mesh: " + File$ 
	Return tmp
End Function   

;don't use in LoadRMesh, as Reg does this manually there. If you wanna fuck around with the logic in that function, be my guest 
Function LoadTexture_Strict(File$,flags=1)
	Local I_Loc.Loc = First Loc
	
	If I_Loc\Localized And FileType(I_Loc\LangPath + file$)=1 Then
		Return LoadTexture(I_Loc\LangPath + File$, flags+(256*(EnableVRam=True)))
	EndIf
	If FileType(File$) <> 1 Then RuntimeError "Texture " + File$ + " not found."
	tmp = LoadTexture(File$, flags+(256*(EnableVRam=True)))
	If tmp = 0 Then RuntimeError "Failed to load Texture: " + File$ 
	Return tmp 
End Function   

Function LoadBrush_Strict(file$,flags,u#=1.0,v#=1.0)
	Local I_Loc.Loc = First Loc
	
	If I_Loc\Localized And FileType(I_Loc\LangPath + file$)=1 Then
		Return LoadBrush(I_Loc\LangPath + file$, flags, u, v)
	EndIf
	If FileType(file$)<>1 Then RuntimeError "Brush Texture " + file$ + "not found."
	tmp = LoadBrush(file$, flags, u, v)
	If tmp = 0 Then RuntimeError "Failed to load Brush: " + file$ 
	Return tmp 
End Function 

Function LoadFont_Strict(file$="Tahoma", height=13, bold=0, italic=0, underline=0)
	If FileType(file$)<>1 Then RuntimeError "Font " + file$ + " not found."
	tmp = LoadFont(file, height, bold, italic, underline)
	If tmp = 0 Then RuntimeError "Failed to load Font: " + file$ 
	Return tmp
End Function