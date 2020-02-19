Graphics3D 640,480,0,2

AppTitle "Convert SCP-CB rooms (B3D generated by 3DWS) to RMESH AND Optimize Lightmaps (convert bmp to png)"

Const HIT_MAP%=1
Const HIT_PLAYER%=2

Collisions HIT_PLAYER,HIT_MAP,2,2

Function StripPath$(file$) 
	Local name$=""
	If Len(file$)>0 
		For i=Len(file$) To 1 Step -1 
			
			mi$=Mid$(file$,i,1) 
			If mi$="\" Or mi$="/" Then Return name$
			
			name$=mi$+name$ 
		Next 
		
	EndIf 
	
	Return name$ 
End Function 

Function StripFilename$(file$)
	Local mi$=""
	Local lastSlash%=0
	If Len(file)>0
		For i%=1 To Len(file)
			mi=Mid(file$,i,1)
			If mi="\" Or mi="/" Then
				lastSlash=i
			EndIf
		Next
	EndIf
	
	Return Left(file,lastSlash)
End Function

Function EntityScaleX#(entity, globl=False) 
	If globl Then TFormVector 1,0,0,entity,0 Else TFormVector 1,0,0,entity,GetParent(entity) 
	Return Sqr(TFormedX()*TFormedX()+TFormedY()*TFormedY()+TFormedZ()*TFormedZ()) 
End Function 

Function EntityScaleY#(entity, globl=False)
	If globl Then TFormVector 0,1,0,entity,0 Else TFormVector 0,1,0,entity,GetParent(entity)  
	Return Sqr(TFormedX()*TFormedX()+TFormedY()*TFormedY()+TFormedZ()*TFormedZ()) 
End Function 

Function EntityScaleZ#(entity, globl=False)
	If globl Then TFormVector 0,0,1,entity,0 Else TFormVector 0,0,1,entity,GetParent(entity)  
	Return Sqr(TFormedX()*TFormedX()+TFormedY()*TFormedY()+TFormedZ()*TFormedZ()) 
End Function

Function Piece$(s$,entry,char$=" ")
	While Instr(s,char+char)
		s=Replace(s,char+char,char)
	Wend
	For n=1 To entry-1
		p=Instr(s,char)
		s=Right(s,Len(s)-p)
	Next
	p=Instr(s,char)
	If p<1
		a$=s
	Else
		a=Left(s,p-1)
	EndIf
	Return a
End Function

Function KeyValue$(entity,key$,defaultvalue$="")
	properties$=EntityName(entity)
	properties$=Replace(properties$,Chr(13),"")
	key$=Lower(key)
	Repeat
		p=Instr(properties,Chr(10))
		If p Then test$=(Left(properties,p-1)) Else test=properties
		testkey$=Piece(test,1,"=")
		testkey=Trim(testkey)
		testkey=Replace(testkey,Chr(34),"")
		testkey=Lower(testkey)
		If testkey=key Then
			value$=Piece(test,2,"=")
			value$=Trim(value$)
			value$=Replace(value$,Chr(34),"")
			Return value
		EndIf
		If Not p Then Return defaultvalue$
		properties=Right(properties,Len(properties)-p)
	Forever 
End Function

Function isAlpha%(tex%) ;detect transparency in textures
	Local temp1s$=StripPath(TextureName(tex))
	Local temp1i%
	If Instr(temp1s,".png")<>0 Or Instr(temp1s,".tga")<>0 Or Instr(temp1s,".tpic")<>0 Then ;texture is PNG or TARGA
		LockBuffer(TextureBuffer(tex))
		For x%=0 To TextureWidth(tex)-1
			For y%=0 To TextureHeight(tex)-1
				temp1i=ReadPixelFast(x,y,TextureBuffer(tex))
				temp1i=temp1i Shr 24
				If temp1i<255 Then
					UnlockBuffer(TextureBuffer(tex))
					Return 3 ;texture has transparency
				EndIf
			Next
		Next
		UnlockBuffer(TextureBuffer(tex))
		Return 1 ;texture is opaque
	Else If Instr(temp1s,"_lm")<>0 Then ;texture is a lightmap
		Return 2
	EndIf
	Return 1 ;texture is opaque
End Function

Function SaveRoomMesh(BaseMesh%,filename$) ;base mesh should be a 3D World Studio mesh
	
	DebugLog filename + "___" + BaseMesh
	
	Local node%,classname$
	Local surf%,brush%,tex%,texname$
	
	Local temp1i%
	
	Local tempmesh% = BaseMesh
	
	Local f% = WriteFile(filename)
	
	Local drawnmesh% = CreateMesh()
	Local vismesh% = CreateMesh()
	Local hiddenmesh% = CreateMesh()
	Local TriggerboxAmount% = 0
	Local Triggerbox[128]
	Local TriggerboxName$[128]
	
	DebugLog CountChildren(tempmesh)
	
	For c%=1 To CountChildren(tempmesh)
		
		node=GetChild(tempmesh,c)
		classname$=Lower(KeyValue(node,"classname"))
		
		Select classname
			Case "mesh"
				ScaleMesh node,EntityScaleX(node),EntityScaleY(node),EntityScaleZ(node)
				RotateMesh node,EntityPitch(node),EntityYaw(node),EntityRoll(node)
				PositionMesh node,EntityX(node),EntityY(node),EntityZ(node)
				AddMesh node,drawnmesh
			Case "brush"
				RotateMesh node,EntityPitch(node),EntityYaw(node),EntityRoll(node)
				PositionMesh node,EntityX(node),EntityY(node),EntityZ(node)
				AddMesh node,drawnmesh
			Case "mesh_vis"
				ScaleMesh node,EntityScaleX(node),EntityScaleY(node),EntityScaleZ(node)
				RotateMesh node,EntityPitch(node),EntityYaw(node),EntityRoll(node)
				PositionMesh node,EntityX(node),EntityY(node),EntityZ(node)
				AddMesh node,vismesh
			Case "field_vis"
				RotateMesh node,EntityPitch(node),EntityYaw(node),EntityRoll(node)
				PositionMesh node,EntityX(node),EntityY(node),EntityZ(node)
				AddMesh node,vismesh
			Case "field_hit"
				RotateMesh node,EntityPitch(node),EntityYaw(node),EntityRoll(node)
				PositionMesh node,EntityX(node),EntityY(node),EntityZ(node)
				AddMesh node,hiddenmesh
			Case "trigger"
				Triggerbox[TriggerboxAmount] = CreateMesh()
				RotateMesh node,EntityPitch(node),EntityYaw(node),EntityRoll(node)
				PositionMesh node,EntityX(node),EntityY(node),EntityZ(node)
				AddMesh node,Triggerbox[TriggerboxAmount]
				TriggerboxName[TriggerboxAmount] = String(KeyValue(node,"event","event"),1)
				TriggerboxAmount=TriggerboxAmount+1
		End Select
		
	Next
	
	If TriggerboxAmount% = 0
		WriteString f,"NewRoomMesh"
	Else
		WriteString f,"NewRoomMesh.HasTriggerBox"
	EndIf
	
	;Drawmeshes
	
	WriteInt f,CountSurfaces(drawnmesh) : DebugLog "Drawmeshes: " + CountSurfaces(drawnmesh)
	For i%=1 To CountSurfaces(drawnmesh)
		surf=GetSurface(drawnmesh,i)
		brush=GetSurfaceBrush(surf)
		tex=0
		tex=GetBrushTexture(brush,0)
		If tex<>0 Then
			WriteByte(f,isAlpha(tex))
			texname=TextureName(tex)
			WriteString f,StripPath(texname)
			FreeTexture tex
		Else
			WriteByte(f,0)
		EndIf
		
		tex=0
		tex=GetBrushTexture(brush,1)
		If tex<>0 Then
			WriteByte(f,isAlpha(tex))
			texname=TextureName(tex)
			WriteString f,StripPath(texname)
			FreeTexture tex
		Else
			WriteByte(f,0)
		EndIf
		
		FreeBrush brush
		
		WriteInt f,CountVertices(surf)
		For j%=0 To CountVertices(surf)-1
			
			;world coords
			WriteFloat f,VertexX(surf,j)
			WriteFloat f,VertexY(surf,j)
			WriteFloat f,VertexZ(surf,j)
			
			;texture coords
			WriteFloat f,VertexU(surf,j,0)
			WriteFloat f,VertexV(surf,j,0)
			
			WriteFloat f,VertexU(surf,j,1)
			WriteFloat f,VertexV(surf,j,1)
			
			;colors
			WriteByte f,VertexRed(surf,j)
			WriteByte f,VertexGreen(surf,j)
			WriteByte f,VertexBlue(surf,j)
		Next
		
		WriteInt f,CountTriangles(surf)
		For j%=0 To CountTriangles(surf)-1
			WriteInt f,TriangleVertex(surf,j,0)
			WriteInt f,TriangleVertex(surf,j,1)
			WriteInt f,TriangleVertex(surf,j,2)
		Next
		
	Next
	
	
	;Vismeshes
	
	WriteInt f,CountSurfaces(vismesh) : DebugLog "Vismeshes: " + CountSurfaces(vismesh)
	For i%=1 To CountSurfaces(vismesh)
		surf=GetSurface(vismesh,i)
		brush=GetSurfaceBrush(surf)
		tex=0
		tex=GetBrushTexture(brush,0)
		If tex<>0 Then
			WriteByte(f,isAlpha(tex))
			texname=TextureName(tex)
			WriteString f,StripPath(texname)
			FreeTexture tex
		Else
			WriteByte(f,0)
		EndIf
		
		tex=0
		tex=GetBrushTexture(brush,1)
		If tex<>0 Then
			WriteByte(f,isAlpha(tex))
			texname=TextureName(tex)
			WriteString f,StripPath(texname)
			FreeTexture tex
		Else
			WriteByte(f,0)
		EndIf
		
		FreeBrush brush
		
		WriteInt f,CountVertices(surf)
		For j%=0 To CountVertices(surf)-1
			
			;world coords
			WriteFloat f,VertexX(surf,j)
			WriteFloat f,VertexY(surf,j)
			WriteFloat f,VertexZ(surf,j)
			
			;texture coords
			WriteFloat f,VertexU(surf,j,0)
			WriteFloat f,VertexV(surf,j,0)
			
			WriteFloat f,VertexU(surf,j,1)
			WriteFloat f,VertexV(surf,j,1)
			
			;colors
			WriteByte f,VertexRed(surf,j)
			WriteByte f,VertexGreen(surf,j)
			WriteByte f,VertexBlue(surf,j)
		Next
		
		WriteInt f,CountTriangles(surf)
		For j%=0 To CountTriangles(surf)-1
			WriteInt f,TriangleVertex(surf,j,0)
			WriteInt f,TriangleVertex(surf,j,1)
			WriteInt f,TriangleVertex(surf,j,2)
		Next
		
	Next
	
	
	;Hiddenmeshes
	
	WriteInt f,CountSurfaces(hiddenmesh) : DebugLog "Hiddenmeshes: " + CountSurfaces(hiddenmesh)
	For i%=1 To CountSurfaces(hiddenmesh)
		surf=GetSurface(hiddenmesh,i)
		WriteInt f,CountVertices(surf)
		For j%=0 To CountVertices(surf)-1
			;world coords
			WriteFloat f,VertexX(surf,j)
			WriteFloat f,VertexY(surf,j)
			WriteFloat f,VertexZ(surf,j)
		Next
		
		WriteInt f,CountTriangles(surf)
		For j%=0 To CountTriangles(surf)-1
			WriteInt f,TriangleVertex(surf,j,0)
			WriteInt f,TriangleVertex(surf,j,1)
			WriteInt f,TriangleVertex(surf,j,2)
		Next
	Next
	
	If TriggerboxAmount > 0
		WriteInt f,TriggerboxAmount
		For z=0 To TriggerboxAmount-1
			WriteInt f,CountSurfaces(Triggerbox[z]) : DebugLog "Triggerboxes: " + CountSurfaces(Triggerbox[z])
			For i%=1 To CountSurfaces(Triggerbox[z])
				surf=GetSurface(Triggerbox[z],i)
				WriteInt f,CountVertices(surf)
				For j%=0 To CountVertices(surf)-1
					;world coords
					WriteFloat f,VertexX(surf,j)
					WriteFloat f,VertexY(surf,j)
					WriteFloat f,VertexZ(surf,j)
				Next
				
				WriteInt f,CountTriangles(surf)
				For j%=0 To CountTriangles(surf)-1
					WriteInt f,TriangleVertex(surf,j,0)
					WriteInt f,TriangleVertex(surf,j,1)
					WriteInt f,TriangleVertex(surf,j,2)
				Next
			Next
			WriteString f,TriggerboxName[z]
		Next
	EndIf
	
	;Point Entities
	
	temp1i=0
	
	For c%=1 To CountChildren(tempmesh)
		node=GetChild(tempmesh,c)	
		classname$=Lower(KeyValue(node,"classname"))
		
		Select classname
			Case "screen","waypoint","light","spotlight","soundemitter","playerstart","model"
				temp1i=temp1i+1
		End Select
		
	Next
	
	WriteInt f,temp1i
	
	For c%=1 To CountChildren(tempmesh)
		
		node=GetChild(tempmesh,c)	
		classname$=Lower(KeyValue(node,"classname"))
		
		Select classname
			Case "screen"
				WriteString f,classname
				
				WriteFloat f,EntityX(node)
				WriteFloat f,EntityY(node)
				WriteFloat f,EntityZ(node)
				
				WriteString f,KeyValue(node,"imgpath","")
			Case "waypoint"
				WriteString f,classname
				
				WriteFloat f,EntityX(node)
				WriteFloat f,EntityY(node)
				WriteFloat f,EntityZ(node)
			Case "light"
				WriteString f,classname
				
				WriteFloat f,EntityX(node)
				WriteFloat f,EntityY(node)
				WriteFloat f,EntityZ(node)
				
				WriteFloat f,Float(KeyValue(node,"range","1"))
				WriteString f,KeyValue(node,"color","255 255 255")
				WriteFloat f,Float(KeyValue(node,"intensity","1.0"))
			Case "spotlight"
				WriteString f,classname
				
				WriteFloat f,EntityX(node)
				WriteFloat f,EntityY(node)
				WriteFloat f,EntityZ(node)
				
				WriteFloat f,Float(KeyValue(node,"range","1"))
				WriteString f,KeyValue(node,"color","255 255 255")
				WriteFloat f,Float(KeyValue(node,"intensity","1.0"))
				WriteString f,KeyValue(node,"angles","0 0 0")
				
				WriteInt f,Int(KeyValue(node,"innerconeangle",""))
				WriteInt f,Int(KeyValue(node,"outerconeangle",""))
			Case "soundemitter"
				WriteString f,classname
				
				WriteFloat f,EntityX(node)
				WriteFloat f,EntityY(node)
				WriteFloat f,EntityZ(node)
				
				WriteInt f,Int(KeyValue(node,"sound","0"))
				WriteFloat f,Float(KeyValue(node,"range","1"))
			Case "playerstart"
				WriteString f,classname
				
				WriteFloat f,EntityX(node)
				WriteFloat f,EntityY(node)
				WriteFloat f,EntityZ(node)
				
				WriteString f,KeyValue(node,"angles","0 0 0")
			Case "model"
				WriteString f,classname
				
				WriteString f,KeyValue(node,"file","")
				
				WriteFloat f,EntityX(node)
				WriteFloat f,EntityY(node)
				WriteFloat f,EntityZ(node)
				
				WriteFloat f,EntityPitch(node)
				WriteFloat f,EntityYaw(node)
				WriteFloat f,EntityRoll(node)
				
				WriteFloat f,EntityScaleX(node)
				WriteFloat f,EntityScaleY(node)
				WriteFloat f,EntityScaleZ(node)
		End Select
		
	Next
	
	WriteString f,"EOF"
	
	CloseFile f
	
	FreeEntity drawnmesh
	FreeEntity hiddenmesh
	
End Function

; INI functions
Function GetINIString$(file$, section$, parameter$)
	Local TemporaryString$ = ""
	Local f = ReadFile(file)
	
	While Not Eof(f)
		If ReadLine(f) = "["+section+"]" Then
			Repeat 
				TemporaryString = ReadLine(f)
				If Trim( Left(TemporaryString, Max(Instr(TemporaryString,"=")-1,0)) ) = parameter Then
					CloseFile f
					Return Trim( Right(TemporaryString,Len(TemporaryString)-Instr(TemporaryString,"=")) )
				EndIf
			Until Left(TemporaryString,1) = "[" Or Eof(f)
			CloseFile f
			Return ""
		EndIf
	Wend
	
	CloseFile f
End Function

Function GetINIInt%(file$, section$, parameter$)
	Local strtemp$ = Lower(GetINIString(file$, section$, parameter$))
	
	Select strtemp
		Case "true"
			Return 1
		Case "false"
			Return 0
		Default
			Return Int(strtemp)
	End Select
	Return 
End Function

Function GetINIFloat#(file$, section$, parameter$)
	Return GetINIString(file$, section$, parameter$)
End Function

Function PutINIValue%(INI_sAppName$, INI_sSection$, INI_sKey$, INI_sValue$)
	
; Returns: True (Success) or False (Failed)
	
	INI_sSection = "[" + Trim$(INI_sSection) + "]"
	INI_sUpperSection$ = Upper$(INI_sSection)
	INI_sKey = Trim$(INI_sKey)
	INI_sValue = Trim$(INI_sValue)
	INI_sFilename$ = CurrentDir$() + "\"  + INI_sAppName
	
; Retrieve the INI data (if it exists)
	
	INI_sContents$= INI_FileToString(INI_sFilename)
	
; (Re)Create the INI file updating/adding the SECTION, KEY and VALUE
	
	INI_bWrittenKey% = False
	INI_bSectionFound% = False
	INI_sCurrentSection$ = ""
	
	INI_lFileHandle = WriteFile(INI_sFilename)
	If INI_lFileHandle = 0 Then Return False ; Create file failed!
	
	INI_lOldPos% = 1
	INI_lPos% = Instr(INI_sContents, Chr$(0))
	
	While (INI_lPos <> 0)
		
		INI_sTemp$ =Trim$(Mid$(INI_sContents, INI_lOldPos, (INI_lPos - INI_lOldPos)))
		
		If (INI_sTemp <> "") Then
			
			If Left$(INI_sTemp, 1) = "[" And Right$(INI_sTemp, 1) = "]" Then
				
				; Process SECTION
				
				If (INI_sCurrentSection = INI_sUpperSection) And (INI_bWrittenKey = False) Then
					INI_bWrittenKey = INI_CreateKey(INI_lFileHandle, INI_sKey, INI_sValue)
				EndIf
				INI_sCurrentSection = Upper$(INI_CreateSection(INI_lFileHandle, INI_sTemp))
				If (INI_sCurrentSection = INI_sUpperSection) Then INI_bSectionFound = True
				
			Else
				
				; KEY=VALUE
				
				lEqualsPos% = Instr(INI_sTemp, "=")
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
		
		; Move through the INI file...
		
		INI_lOldPos = INI_lPos + 1
		INI_lPos% = Instr(INI_sContents, Chr$(0), INI_lOldPos)
		
	Wend
	
	; KEY wasn't found in the INI file - Append a new SECTION if required and create our KEY=VALUE line
	
	If (INI_bWrittenKey = False) Then
		If (INI_bSectionFound = False) Then INI_CreateSection INI_lFileHandle, INI_sSection
		INI_CreateKey INI_lFileHandle, INI_sKey, INI_sValue
	EndIf
	
	CloseFile INI_lFileHandle
	
	Return True ; Success
	
End Function


Function INI_FileToString$(INI_sFilename$)
	
	INI_sString$ = ""
	INI_lFileHandle% = ReadFile(INI_sFilename)
	If INI_lFileHandle <> 0 Then
		While Not(Eof(INI_lFileHandle))
			INI_sString = INI_sString + ReadLine$(INI_lFileHandle) + Chr$(0)
		Wend
		CloseFile INI_lFileHandle
	EndIf
	Return INI_sString
	
End Function

Function INI_CreateSection$(INI_lFileHandle%, INI_sNewSection$)
	
	If FilePos(INI_lFileHandle) <> 0 Then WriteLine INI_lFileHandle, "" ; Blank line between sections
	WriteLine INI_lFileHandle, INI_sNewSection
	Return INI_sNewSection
	
End Function

Function INI_CreateKey%(INI_lFileHandle%, INI_sKey$, INI_sValue$)
	
	WriteLine INI_lFileHandle, INI_sKey + "=" + INI_sValue
	Return True
	
End Function


; LIGHTMAP STUFF

Const FIF_UNKNOWN = -1
Const FIF_BMP = 0
Const FIF_ICO = 1
Const FIF_JPEG = 2
Const FIF_JNG = 3
Const FIF_KOALA = 4
Const FIF_LBM = 5
Const FIF_IFF = FIF_LBM
Const FIF_MNG = 6
Const FIF_PBM = 7
Const FIF_PBMRAW = 8
Const FIF_PCD = 9
Const FIF_PCX = 10
Const FIF_PGM = 11
Const FIF_PGMRAW = 12
Const FIF_PNG = 13
Const FIF_PPM = 14
Const FIF_PPMRAW = 15
Const FIF_RAS = 16
Const FIF_TARGA = 17
Const FIF_TIFF = 18
Const FIF_WBMP = 19
Const FIF_PSD = 20
Const FIF_CUT = 21
Const FIF_XBM = 22
Const FIF_XPM = 23
Const FIF_DDS = 24
Const FIF_GIF = 25
Const FIF_HDR = 26
Const FIF_FAXG3	= 27
Const FIF_SGI = 28
Const FIF_EXR = 29
Const FIF_J2K = 30
Const FIF_JP2 = 31
Const FIF_PFM = 32
Const FIF_PICT = 33
Const FIF_RAW = 34

Type Converted
	Field name$
End Type

Function LoadRMesh(file$)
	
	;read the file
	Local f%=ReadFile(file)
	Local fw%=WriteFile(Replace(file,".rmesh","_opt.rmesh"))
	Local i%,j%,k%,x#,y#,z#,yaw#
	Local vertex%
	Local temp1i%,temp2i%,temp3i%
	Local temp1#,temp2#,temp3#
	Local temp1s$,temp2s$
	
	temp2s=ReadString(f)
	WriteString fw,temp2s
	
	file=StripFilename(file)
	
	Local count%,count2%
	;drawn meshes
	
	count = ReadInt(f)
	WriteInt fw,count
	
	Local u#,v#
	
	For i=1 To count ;drawn mesh
		For j=0 To 1
			temp1i=ReadByte(f)
			WriteByte fw,temp1i
			If temp1i<>0 Then
				temp1s=ReadString(f)
				If Instr(temp1s,".bmp")<>0 Then
					done%=0
					For r.Converted = Each Converted
						If r\name=temp1s Then done=1 : Exit
					Next
					If Not done Then
						r.Converted = New Converted
						r\name=temp1s
						loadTex%=FI_Load(FIF_BMP,file+temp1s,0)
						DebugLog temp1s
					EndIf
					temp1s=Replace(temp1s,".bmp",".png")
					If Not done Then
						FI_Save(FIF_PNG,loadTex,file+temp1s,0)
						FI_Unload(loadTex)
					EndIf
				EndIf
				WriteString fw,temp1s
			EndIf
		Next
		
		count2=ReadInt(f) ;vertices
		WriteInt fw,count2
		
		For j%=1 To count2
			;world coords
			x=ReadFloat(f) : y=ReadFloat(f) : z=ReadFloat(f)
			WriteFloat fw,x
			WriteFloat fw,y
			WriteFloat fw,z
			
			;texture coords
			For k%=0 To 1
				u=ReadFloat(f) : v=ReadFloat(f)
				WriteFloat fw,u
				WriteFloat fw,v
			Next
			
			;colors
			temp1i=ReadByte(f)
			temp2i=ReadByte(f)
			temp3i=ReadByte(f)
			WriteByte fw,temp1i
			WriteByte fw,temp2i
			WriteByte fw,temp3i
		Next
		
		count2=ReadInt(f) ;polys
		WriteInt fw,count2
		For j%=1 To count2
			temp1i = ReadInt(f) : temp2i = ReadInt(f) : temp3i = ReadInt(f)
			WriteInt fw,temp1i
			WriteInt fw,temp2i
			WriteInt fw,temp3i
		Next
		
	Next
	
	;vis
	
	count = ReadInt(f)
	WriteInt fw,count
	
	For i=1 To count ;vis mesh
	
		For j=0 To 1
			temp1i=ReadByte(f)
			WriteByte fw,temp1i
			If temp1i<>0 Then
				temp1s=ReadString(f)
				If Instr(temp1s,".bmp")<>0 Then
					done%=0
					For r.Converted = Each Converted
						If r\name=temp1s Then done=1 : Exit
					Next
					If Not done Then
						r.Converted = New Converted
						r\name=temp1s
						loadTex%=FI_Load(FIF_BMP,file+temp1s,0)
						DebugLog temp1s
					EndIf
					temp1s=Replace(temp1s,".bmp",".png")
					If Not done Then
						FI_Save(FIF_PNG,loadTex,file+temp1s,0)
						FI_Unload(loadTex)
					EndIf
				EndIf
				WriteString fw,temp1s
			EndIf
		Next
		
		count2=ReadInt(f) ;vertices
		WriteInt fw,count2
		
		For j%=1 To count2
			;world coords
			x=ReadFloat(f) : y=ReadFloat(f) : z=ReadFloat(f)
			WriteFloat fw,x
			WriteFloat fw,y
			WriteFloat fw,z
			
			;texture coords
			For k%=0 To 1
				u=ReadFloat(f) : v=ReadFloat(f)
				WriteFloat fw,u
				WriteFloat fw,v
			Next
			
			;colors
			temp1i=ReadByte(f)
			temp2i=ReadByte(f)
			temp3i=ReadByte(f)
			WriteByte fw,temp1i
			WriteByte fw,temp2i
			WriteByte fw,temp3i
		Next
		
		count2=ReadInt(f) ;polys
		WriteInt fw,count2
		For j%=1 To count2
			temp1i = ReadInt(f) : temp2i = ReadInt(f) : temp3i = ReadInt(f)
			WriteInt fw,temp1i
			WriteInt fw,temp2i
			WriteInt fw,temp3i
		Next
		
	Next
	
	While (Not Eof(f))
		temp1i=ReadByte(f)
		WriteByte fw,temp1i
	Wend
	
End Function


; ACTUAL STUFF like, you know, the stuff that actually does stuff ~Salvage

SetBuffer BackBuffer()
ClsColor 0,0,0
Color 255,255,255

Local Stri$,TemporaryString$

Stri=Input("Path for the room to be converted: ")
mesh=LoadAnimMesh(Stri)
SaveRoomMesh(mesh,Replace(Stri,".b3d",".rmesh"))
LoadRMesh(Replace(Stri,".b3d",".rmesh"))
Cls
Text 5,5,"Conversion of "+Stri+" complete"
Flip
Delay 1000