xof 0303txt 0032
template XSkinMeshHeader {
 <3cf169ce-ff7c-44ab-93c0-f78f62d172e2>
 WORD nMaxSkinWeightsPerVertex;
 WORD nMaxSkinWeightsPerFace;
 WORD nBones;
}

template VertexDuplicationIndices {
 <b8d65549-d7c9-4995-89cf-53a9a8b031e3>
 DWORD nIndices;
 DWORD nOriginalVertices;
 array DWORD indices[nIndices];
}

template SkinWeights {
 <6f0d123b-bad2-4167-a0d0-80224f25fabb>
 STRING transformNodeName;
 DWORD nWeights;
 array DWORD vertexIndices[nWeights];
 array FLOAT weights[nWeights];
 Matrix4x4 matrixOffset;
}

template AnimTicksPerSecond {
 <9e415a43-7ba6-4a73-8743-b73d47e88476>
 DWORD AnimTicksPerSecond;
}

template FVFData {
 <b6e70a0e-8ef9-4e83-94ad-ecc8b0c04897>
 DWORD dwFVF;
 DWORD nDWords;
 array DWORD data[nDWords];
}


AnimTicksPerSecond {
 24;
}

Frame World {
 

 FrameTransformMatrix {
  1.000000,0.000000,0.000000,0.000000,0.000000,1.000000,0.000000,0.000000,0.000000,0.000000,1.000000,0.000000,0.000000,0.000000,0.000000,1.000000;;
 }

 Frame Mesh1 {
  

  FrameTransformMatrix {
   1.000000,0.000000,0.000000,0.000000,0.000000,1.000000,0.000000,0.000000,0.000000,0.000000,1.000000,0.000000,0.000000,0.000000,0.000000,1.000000;;
  }

  Mesh Mesh1 {
   168;
   32.000000;320.000000;-88.000000;,
   -32.000000;316.000000;-88.000000;,
   32.000000;316.000000;-88.000000;,
   -32.000000;320.000000;-88.000000;,
   32.000000;316.000000;88.000000;,
   -32.000000;320.000000;88.000000;,
   32.000000;320.000000;88.000000;,
   -32.000000;316.000000;88.000000;,
   32.000000;264.000000;-88.000000;,
   -32.000000;260.000000;-88.000000;,
   32.000000;260.000000;-88.000000;,
   -32.000000;264.000000;-88.000000;,
   32.000000;260.000000;88.000000;,
   -32.000000;264.000000;88.000000;,
   32.000000;264.000000;88.000000;,
   -32.000000;260.000000;88.000000;,
   32.000000;200.000000;-88.000000;,
   -32.000000;196.000000;-88.000000;,
   32.000000;196.000000;-88.000000;,
   -32.000000;200.000000;-88.000000;,
   32.000000;196.000000;88.000000;,
   -32.000000;200.000000;88.000000;,
   32.000000;200.000000;88.000000;,
   -32.000000;196.000000;88.000000;,
   32.000000;136.000000;-88.000000;,
   -32.000000;132.000000;-88.000000;,
   32.000000;132.000000;-88.000000;,
   -32.000000;136.000000;-88.000000;,
   32.000000;132.000000;88.000000;,
   -32.000000;136.000000;88.000000;,
   32.000000;136.000000;88.000000;,
   -32.000000;132.000000;88.000000;,
   32.000000;72.000000;-88.000000;,
   -32.000000;68.000000;-88.000000;,
   32.000000;68.000000;-88.000000;,
   -32.000000;72.000000;-88.000000;,
   32.000000;68.000000;88.000000;,
   -32.000000;72.000000;88.000000;,
   32.000000;72.000000;88.000000;,
   -32.000000;68.000000;88.000000;,
   32.000000;324.000000;88.000000;,
   -32.000000;0.056700;88.000000;,
   32.000000;0.056700;88.000000;,
   -32.000000;324.000000;88.000000;,
   32.000000;0.056700;92.000000;,
   -32.000000;324.000000;92.000000;,
   32.000000;324.000000;92.000000;,
   -32.000000;0.056700;92.000000;,
   32.000000;324.000000;-92.000000;,
   -32.000000;0.056700;-92.000000;,
   32.000000;0.056700;-92.000000;,
   -32.000000;324.000000;-92.000000;,
   32.000000;0.056700;-88.000000;,
   -32.000000;324.000000;-88.000000;,
   32.000000;324.000000;-88.000000;,
   -32.000000;0.056700;-88.000000;,
   32.000000;320.000000;-88.000000;,
   32.000000;320.000000;-88.000000;,
   -32.000000;316.000000;-88.000000;,
   -32.000000;316.000000;-88.000000;,
   32.000000;316.000000;-88.000000;,
   32.000000;316.000000;-88.000000;,
   -32.000000;320.000000;-88.000000;,
   -32.000000;320.000000;-88.000000;,
   32.000000;316.000000;88.000000;,
   32.000000;316.000000;88.000000;,
   -32.000000;320.000000;88.000000;,
   -32.000000;320.000000;88.000000;,
   32.000000;320.000000;88.000000;,
   32.000000;320.000000;88.000000;,
   -32.000000;316.000000;88.000000;,
   -32.000000;316.000000;88.000000;,
   32.000000;264.000000;-88.000000;,
   32.000000;264.000000;-88.000000;,
   -32.000000;260.000000;-88.000000;,
   -32.000000;260.000000;-88.000000;,
   32.000000;260.000000;-88.000000;,
   32.000000;260.000000;-88.000000;,
   -32.000000;264.000000;-88.000000;,
   -32.000000;264.000000;-88.000000;,
   32.000000;260.000000;88.000000;,
   32.000000;260.000000;88.000000;,
   -32.000000;264.000000;88.000000;,
   -32.000000;264.000000;88.000000;,
   32.000000;264.000000;88.000000;,
   32.000000;264.000000;88.000000;,
   -32.000000;260.000000;88.000000;,
   -32.000000;260.000000;88.000000;,
   32.000000;200.000000;-88.000000;,
   32.000000;200.000000;-88.000000;,
   -32.000000;196.000000;-88.000000;,
   -32.000000;196.000000;-88.000000;,
   32.000000;196.000000;-88.000000;,
   32.000000;196.000000;-88.000000;,
   -32.000000;200.000000;-88.000000;,
   -32.000000;200.000000;-88.000000;,
   32.000000;196.000000;88.000000;,
   32.000000;196.000000;88.000000;,
   -32.000000;200.000000;88.000000;,
   -32.000000;200.000000;88.000000;,
   32.000000;200.000000;88.000000;,
   32.000000;200.000000;88.000000;,
   -32.000000;196.000000;88.000000;,
   -32.000000;196.000000;88.000000;,
   32.000000;136.000000;-88.000000;,
   32.000000;136.000000;-88.000000;,
   -32.000000;132.000000;-88.000000;,
   -32.000000;132.000000;-88.000000;,
   32.000000;132.000000;-88.000000;,
   32.000000;132.000000;-88.000000;,
   -32.000000;136.000000;-88.000000;,
   -32.000000;136.000000;-88.000000;,
   32.000000;132.000000;88.000000;,
   32.000000;132.000000;88.000000;,
   -32.000000;136.000000;88.000000;,
   -32.000000;136.000000;88.000000;,
   32.000000;136.000000;88.000000;,
   32.000000;136.000000;88.000000;,
   -32.000000;132.000000;88.000000;,
   -32.000000;132.000000;88.000000;,
   32.000000;72.000000;-88.000000;,
   32.000000;72.000000;-88.000000;,
   -32.000000;68.000000;-88.000000;,
   -32.000000;68.000000;-88.000000;,
   32.000000;68.000000;-88.000000;,
   32.000000;68.000000;-88.000000;,
   -32.000000;72.000000;-88.000000;,
   -32.000000;72.000000;-88.000000;,
   32.000000;68.000000;88.000000;,
   32.000000;68.000000;88.000000;,
   -32.000000;72.000000;88.000000;,
   -32.000000;72.000000;88.000000;,
   32.000000;72.000000;88.000000;,
   32.000000;72.000000;88.000000;,
   -32.000000;68.000000;88.000000;,
   -32.000000;68.000000;88.000000;,
   32.000000;324.000000;88.000000;,
   32.000000;324.000000;88.000000;,
   -32.000000;0.056700;88.000000;,
   -32.000000;0.056700;88.000000;,
   32.000000;0.056700;88.000000;,
   32.000000;0.056700;88.000000;,
   -32.000000;324.000000;88.000000;,
   -32.000000;324.000000;88.000000;,
   32.000000;0.056700;92.000000;,
   32.000000;0.056700;92.000000;,
   -32.000000;324.000000;92.000000;,
   -32.000000;324.000000;92.000000;,
   32.000000;324.000000;92.000000;,
   32.000000;324.000000;92.000000;,
   -32.000000;0.056700;92.000000;,
   -32.000000;0.056700;92.000000;,
   32.000000;324.000000;-92.000000;,
   32.000000;324.000000;-92.000000;,
   -32.000000;0.056700;-92.000000;,
   -32.000000;0.056700;-92.000000;,
   32.000000;0.056700;-92.000000;,
   32.000000;0.056700;-92.000000;,
   -32.000000;324.000000;-92.000000;,
   -32.000000;324.000000;-92.000000;,
   32.000000;0.056700;-88.000000;,
   32.000000;0.056700;-88.000000;,
   -32.000000;324.000000;-88.000000;,
   -32.000000;324.000000;-88.000000;,
   32.000000;324.000000;-88.000000;,
   32.000000;324.000000;-88.000000;,
   -32.000000;0.056700;-88.000000;,
   -32.000000;0.056700;-88.000000;;
   84;
   3;57,61,59;,
   3;63,57,59;,
   3;65,69,67;,
   3;71,65,67;,
   3;64,70,58;,
   3;60,64,58;,
   3;66,68,56;,
   3;62,66,56;,
   3;5,3,1;,
   3;7,5,1;,
   3;0,6,4;,
   3;2,0,4;,
   3;73,77,75;,
   3;79,73,75;,
   3;81,85,83;,
   3;87,81,83;,
   3;80,86,74;,
   3;76,80,74;,
   3;82,84,72;,
   3;78,82,72;,
   3;13,11,9;,
   3;15,13,9;,
   3;8,14,12;,
   3;10,8,12;,
   3;89,93,91;,
   3;95,89,91;,
   3;97,101,99;,
   3;103,97,99;,
   3;96,102,90;,
   3;92,96,90;,
   3;98,100,88;,
   3;94,98,88;,
   3;21,19,17;,
   3;23,21,17;,
   3;16,22,20;,
   3;18,16,20;,
   3;105,109,107;,
   3;111,105,107;,
   3;113,117,115;,
   3;119,113,115;,
   3;112,118,106;,
   3;108,112,106;,
   3;114,116,104;,
   3;110,114,104;,
   3;29,27,25;,
   3;31,29,25;,
   3;24,30,28;,
   3;26,24,28;,
   3;121,125,123;,
   3;127,121,123;,
   3;129,133,131;,
   3;135,129,131;,
   3;128,134,122;,
   3;124,128,122;,
   3;130,132,120;,
   3;126,130,120;,
   3;37,35,33;,
   3;39,37,33;,
   3;32,38,36;,
   3;34,32,36;,
   3;137,141,139;,
   3;143,137,139;,
   3;145,149,147;,
   3;151,145,147;,
   3;144,150,138;,
   3;140,144,138;,
   3;146,148,136;,
   3;142,146,136;,
   3;45,43,41;,
   3;47,45,41;,
   3;40,46,44;,
   3;42,40,44;,
   3;153,157,155;,
   3;159,153,155;,
   3;161,165,163;,
   3;167,161,163;,
   3;160,166,154;,
   3;156,160,154;,
   3;162,164,152;,
   3;158,162,152;,
   3;53,51,49;,
   3;55,53,49;,
   3;48,54,52;,
   3;50,48,52;;

   MeshNormals {
    168;
    0.577350;0.577350;-0.577350;,
    -0.577350;-0.577350;-0.577350;,
    0.577350;-0.577350;-0.577350;,
    -0.577350;0.577350;-0.577350;,
    0.577350;-0.577350;0.577350;,
    -0.577350;0.577350;0.577350;,
    0.577350;0.577350;0.577350;,
    -0.577350;-0.577350;0.577350;,
    0.577350;0.577350;-0.577350;,
    -0.577350;-0.577350;-0.577350;,
    0.577350;-0.577350;-0.577350;,
    -0.577350;0.577350;-0.577350;,
    0.577350;-0.577350;0.577350;,
    -0.577350;0.577350;0.577350;,
    0.577350;0.577350;0.577350;,
    -0.577350;-0.577350;0.577350;,
    0.577350;0.577350;-0.577350;,
    -0.577350;-0.577350;-0.577350;,
    0.577350;-0.577350;-0.577350;,
    -0.577350;0.577350;-0.577350;,
    0.577350;-0.577350;0.577350;,
    -0.577350;0.577350;0.577350;,
    0.577350;0.577350;0.577350;,
    -0.577350;-0.577350;0.577350;,
    0.577350;0.577350;-0.577350;,
    -0.577350;-0.577350;-0.577350;,
    0.577350;-0.577350;-0.577350;,
    -0.577350;0.577350;-0.577350;,
    0.577350;-0.577350;0.577350;,
    -0.577350;0.577350;0.577350;,
    0.577350;0.577350;0.577350;,
    -0.577350;-0.577350;0.577350;,
    0.577350;0.577350;-0.577350;,
    -0.577350;-0.577350;-0.577350;,
    0.577350;-0.577350;-0.577350;,
    -0.577350;0.577350;-0.577350;,
    0.577350;-0.577350;0.577350;,
    -0.577350;0.577350;0.577350;,
    0.577350;0.577350;0.577350;,
    -0.577350;-0.577350;0.577350;,
    0.577350;0.577350;-0.577350;,
    -0.577350;-0.577350;-0.577350;,
    0.577350;-0.577350;-0.577350;,
    -0.577350;0.577350;-0.577350;,
    0.577350;-0.577350;0.577350;,
    -0.577350;0.577350;0.577350;,
    0.577350;0.577350;0.577350;,
    -0.577350;-0.577350;0.577350;,
    0.577350;0.577350;-0.577350;,
    -0.577350;-0.577350;-0.577350;,
    0.577350;-0.577350;-0.577350;,
    -0.577350;0.577350;-0.577350;,
    0.577350;-0.577350;0.577350;,
    -0.577350;0.577350;0.577350;,
    0.577350;0.577350;0.577350;,
    -0.577350;-0.577350;0.577350;,
    0.577350;0.577350;-0.577350;,
    0.577350;0.577350;-0.577350;,
    -0.577350;-0.577350;-0.577350;,
    -0.577350;-0.577350;-0.577350;,
    0.577350;-0.577350;-0.577350;,
    0.577350;-0.577350;-0.577350;,
    -0.577350;0.577350;-0.577350;,
    -0.577350;0.577350;-0.577350;,
    0.577350;-0.577350;0.577350;,
    0.577350;-0.577350;0.577350;,
    -0.577350;0.577350;0.577350;,
    -0.577350;0.577350;0.577350;,
    0.577350;0.577350;0.577350;,
    0.577350;0.577350;0.577350;,
    -0.577350;-0.577350;0.577350;,
    -0.577350;-0.577350;0.577350;,
    0.577350;0.577350;-0.577350;,
    0.577350;0.577350;-0.577350;,
    -0.577350;-0.577350;-0.577350;,
    -0.577350;-0.577350;-0.577350;,
    0.577350;-0.577350;-0.577350;,
    0.577350;-0.577350;-0.577350;,
    -0.577350;0.577350;-0.577350;,
    -0.577350;0.577350;-0.577350;,
    0.577350;-0.577350;0.577350;,
    0.577350;-0.577350;0.577350;,
    -0.577350;0.577350;0.577350;,
    -0.577350;0.577350;0.577350;,
    0.577350;0.577350;0.577350;,
    0.577350;0.577350;0.577350;,
    -0.577350;-0.577350;0.577350;,
    -0.577350;-0.577350;0.577350;,
    0.577350;0.577350;-0.577350;,
    0.577350;0.577350;-0.577350;,
    -0.577350;-0.577350;-0.577350;,
    -0.577350;-0.577350;-0.577350;,
    0.577350;-0.577350;-0.577350;,
    0.577350;-0.577350;-0.577350;,
    -0.577350;0.577350;-0.577350;,
    -0.577350;0.577350;-0.577350;,
    0.577350;-0.577350;0.577350;,
    0.577350;-0.577350;0.577350;,
    -0.577350;0.577350;0.577350;,
    -0.577350;0.577350;0.577350;,
    0.577350;0.577350;0.577350;,
    0.577350;0.577350;0.577350;,
    -0.577350;-0.577350;0.577350;,
    -0.577350;-0.577350;0.577350;,
    0.577350;0.577350;-0.577350;,
    0.577350;0.577350;-0.577350;,
    -0.577350;-0.577350;-0.577350;,
    -0.577350;-0.577350;-0.577350;,
    0.577350;-0.577350;-0.577350;,
    0.577350;-0.577350;-0.577350;,
    -0.577350;0.577350;-0.577350;,
    -0.577350;0.577350;-0.577350;,
    0.577350;-0.577350;0.577350;,
    0.577350;-0.577350;0.577350;,
    -0.577350;0.577350;0.577350;,
    -0.577350;0.577350;0.577350;,
    0.577350;0.577350;0.577350;,
    0.577350;0.577350;0.577350;,
    -0.577350;-0.577350;0.577350;,
    -0.577350;-0.577350;0.577350;,
    0.577350;0.577350;-0.577350;,
    0.577350;0.577350;-0.577350;,
    -0.577350;-0.577350;-0.577350;,
    -0.577350;-0.577350;-0.577350;,
    0.577350;-0.577350;-0.577350;,
    0.577350;-0.577350;-0.577350;,
    -0.577350;0.577350;-0.577350;,
    -0.577350;0.577350;-0.577350;,
    0.577350;-0.577350;0.577350;,
    0.577350;-0.577350;0.577350;,
    -0.577350;0.577350;0.577350;,
    -0.577350;0.577350;0.577350;,
    0.577350;0.577350;0.577350;,
    0.577350;0.577350;0.577350;,
    -0.577350;-0.577350;0.577350;,
    -0.577350;-0.577350;0.577350;,
    0.577350;0.577350;-0.577350;,
    0.577350;0.577350;-0.577350;,
    -0.577350;-0.577350;-0.577350;,
    -0.577350;-0.577350;-0.577350;,
    0.577350;-0.577350;-0.577350;,
    0.577350;-0.577350;-0.577350;,
    -0.577350;0.577350;-0.577350;,
    -0.577350;0.577350;-0.577350;,
    0.577350;-0.577350;0.577350;,
    0.577350;-0.577350;0.577350;,
    -0.577350;0.577350;0.577350;,
    -0.577350;0.577350;0.577350;,
    0.577350;0.577350;0.577350;,
    0.577350;0.577350;0.577350;,
    -0.577350;-0.577350;0.577350;,
    -0.577350;-0.577350;0.577350;,
    0.577350;0.577350;-0.577350;,
    0.577350;0.577350;-0.577350;,
    -0.577350;-0.577350;-0.577350;,
    -0.577350;-0.577350;-0.577350;,
    0.577350;-0.577350;-0.577350;,
    0.577350;-0.577350;-0.577350;,
    -0.577350;0.577350;-0.577350;,
    -0.577350;0.577350;-0.577350;,
    0.577350;-0.577350;0.577350;,
    0.577350;-0.577350;0.577350;,
    -0.577350;0.577350;0.577350;,
    -0.577350;0.577350;0.577350;,
    0.577350;0.577350;0.577350;,
    0.577350;0.577350;0.577350;,
    -0.577350;-0.577350;0.577350;,
    -0.577350;-0.577350;0.577350;;
    84;
    3;57,61,59;,
    3;63,57,59;,
    3;65,69,67;,
    3;71,65,67;,
    3;64,70,58;,
    3;60,64,58;,
    3;66,68,56;,
    3;62,66,56;,
    3;5,3,1;,
    3;7,5,1;,
    3;0,6,4;,
    3;2,0,4;,
    3;73,77,75;,
    3;79,73,75;,
    3;81,85,83;,
    3;87,81,83;,
    3;80,86,74;,
    3;76,80,74;,
    3;82,84,72;,
    3;78,82,72;,
    3;13,11,9;,
    3;15,13,9;,
    3;8,14,12;,
    3;10,8,12;,
    3;89,93,91;,
    3;95,89,91;,
    3;97,101,99;,
    3;103,97,99;,
    3;96,102,90;,
    3;92,96,90;,
    3;98,100,88;,
    3;94,98,88;,
    3;21,19,17;,
    3;23,21,17;,
    3;16,22,20;,
    3;18,16,20;,
    3;105,109,107;,
    3;111,105,107;,
    3;113,117,115;,
    3;119,113,115;,
    3;112,118,106;,
    3;108,112,106;,
    3;114,116,104;,
    3;110,114,104;,
    3;29,27,25;,
    3;31,29,25;,
    3;24,30,28;,
    3;26,24,28;,
    3;121,125,123;,
    3;127,121,123;,
    3;129,133,131;,
    3;135,129,131;,
    3;128,134,122;,
    3;124,128,122;,
    3;130,132,120;,
    3;126,130,120;,
    3;37,35,33;,
    3;39,37,33;,
    3;32,38,36;,
    3;34,32,36;,
    3;137,141,139;,
    3;143,137,139;,
    3;145,149,147;,
    3;151,145,147;,
    3;144,150,138;,
    3;140,144,138;,
    3;146,148,136;,
    3;142,146,136;,
    3;45,43,41;,
    3;47,45,41;,
    3;40,46,44;,
    3;42,40,44;,
    3;153,157,155;,
    3;159,153,155;,
    3;161,165,163;,
    3;167,161,163;,
    3;160,166,154;,
    3;156,160,154;,
    3;162,164,152;,
    3;158,162,152;,
    3;53,51,49;,
    3;55,53,49;,
    3;48,54,52;,
    3;50,48,52;;
   }

   MeshTextureCoords {
    168;
    -0.687500;-1.468800;,
    -0.687500;-1.437500;,
    -0.687500;-1.437500;,
    -0.687500;-1.468800;,
    0.687500;-1.437500;,
    0.687500;-1.468800;,
    0.687500;-1.468800;,
    0.687500;-1.437500;,
    -0.210900;-1.062500;,
    -0.210900;-1.031300;,
    -0.210900;-1.031300;,
    -0.210900;-1.062500;,
    1.164100;-1.031300;,
    1.164100;-1.062500;,
    1.164100;-1.062500;,
    1.164100;-1.031300;,
    -0.687500;-2.031300;,
    -0.687500;-2.000000;,
    -0.687500;-2.000000;,
    -0.687500;-2.031300;,
    0.687500;-2.000000;,
    0.687500;-2.031300;,
    0.687500;-2.031300;,
    0.687500;-2.000000;,
    -0.320300;-1.328100;,
    -0.320300;-1.296900;,
    -0.320300;-1.296900;,
    -0.320300;-1.328100;,
    1.054700;-1.296900;,
    1.054700;-1.328100;,
    1.054700;-1.328100;,
    1.054700;-1.296900;,
    -1.187500;-0.445300;,
    -1.187500;-0.414100;,
    -1.187500;-0.414100;,
    -1.187500;-0.445300;,
    0.187500;-0.414100;,
    0.187500;-0.445300;,
    0.187500;-0.445300;,
    0.187500;-0.414100;,
    0.343800;-0.765600;,
    0.343800;0.499800;,
    0.343800;0.499800;,
    0.343800;-0.765600;,
    0.359400;0.499800;,
    0.359400;-0.765600;,
    0.359400;-0.765600;,
    0.359400;0.499800;,
    -0.359400;-0.765600;,
    -0.359400;0.499800;,
    -0.359400;0.499800;,
    -0.359400;-0.765600;,
    -0.343800;0.499800;,
    -0.343800;-0.765600;,
    -0.343800;-0.765600;,
    -0.343800;0.499800;,
    0.250000;0.687500;,
    0.250000;-1.468800;,
    -0.250000;0.687500;,
    -0.250000;-1.437500;,
    0.250000;0.687500;,
    0.250000;-1.437500;,
    -0.250000;0.687500;,
    -0.250000;-1.468800;,
    0.250000;-0.687500;,
    0.250000;-1.437500;,
    -0.250000;-0.687500;,
    -0.250000;-1.468800;,
    0.250000;-0.687500;,
    0.250000;-1.468800;,
    -0.250000;-0.687500;,
    -0.250000;-1.437500;,
    0.726600;0.687500;,
    0.726600;-1.062500;,
    0.226600;0.687500;,
    0.226600;-1.031300;,
    0.726600;0.687500;,
    0.726600;-1.031300;,
    0.226600;0.687500;,
    0.226600;-1.062500;,
    0.726600;-0.687500;,
    0.726600;-1.031300;,
    0.226600;-0.687500;,
    0.226600;-1.062500;,
    0.726600;-0.687500;,
    0.726600;-1.062500;,
    0.226600;-0.687500;,
    0.226600;-1.031300;,
    -0.070300;0.218800;,
    0.250000;-2.031300;,
    -0.250000;0.218800;,
    -0.250000;-2.000000;,
    0.250000;0.218800;,
    0.250000;-2.000000;,
    -0.570300;0.218800;,
    -0.250000;-2.031300;,
    0.250000;-1.156300;,
    0.250000;-2.000000;,
    -0.570300;-1.156300;,
    -0.250000;-2.031300;,
    -0.070300;-1.156300;,
    0.250000;-2.031300;,
    -0.250000;-1.156300;,
    -0.250000;-2.000000;,
    0.617200;0.421900;,
    0.617200;-1.328100;,
    0.117200;0.421900;,
    0.117200;-1.296900;,
    0.617200;0.421900;,
    0.617200;-1.296900;,
    0.117200;0.421900;,
    0.117200;-1.328100;,
    0.617200;-0.953100;,
    0.617200;-1.296900;,
    0.117200;-0.953100;,
    0.117200;-1.328100;,
    0.617200;-0.953100;,
    0.617200;-1.328100;,
    0.117200;-0.953100;,
    0.117200;-1.296900;,
    -0.250000;0.804700;,
    -0.250000;-0.445300;,
    -0.750000;0.804700;,
    -0.750000;-0.414100;,
    -0.250000;0.804700;,
    -0.250000;-0.414100;,
    -0.750000;0.804700;,
    -0.750000;-0.445300;,
    -0.250000;-0.570300;,
    -0.250000;-0.414100;,
    -0.750000;-0.570300;,
    -0.750000;-0.445300;,
    -0.250000;-0.570300;,
    -0.250000;-0.445300;,
    -0.750000;-0.570300;,
    -0.750000;-0.414100;,
    0.125000;-0.343800;,
    0.125000;-0.765600;,
    -0.125000;-0.343800;,
    -0.125000;0.499800;,
    0.125000;-0.343800;,
    0.125000;0.499800;,
    -0.125000;-0.343800;,
    -0.125000;-0.765600;,
    0.125000;-0.359400;,
    0.125000;0.499800;,
    -0.125000;-0.359400;,
    -0.125000;-0.765600;,
    0.125000;-0.359400;,
    0.125000;-0.765600;,
    -0.125000;-0.359400;,
    -0.125000;0.499800;,
    0.125000;0.359400;,
    0.125000;-0.765600;,
    -0.125000;0.359400;,
    -0.125000;0.499800;,
    0.125000;0.359400;,
    0.125000;0.499800;,
    -0.125000;0.359400;,
    -0.125000;-0.765600;,
    0.125000;0.343800;,
    0.125000;0.499800;,
    -0.125000;0.343800;,
    -0.125000;-0.765600;,
    0.125000;0.343800;,
    0.125000;-0.765600;,
    -0.125000;0.343800;,
    -0.125000;0.499800;;
   }

   VertexDuplicationIndices {
    168;
    56;
    0,
    1,
    2,
    3,
    4,
    5,
    6,
    7,
    8,
    9,
    10,
    11,
    12,
    13,
    14,
    15,
    16,
    17,
    18,
    19,
    20,
    21,
    22,
    23,
    24,
    25,
    26,
    27,
    28,
    29,
    30,
    31,
    32,
    33,
    34,
    35,
    36,
    37,
    38,
    39,
    40,
    41,
    42,
    43,
    44,
    45,
    46,
    47,
    48,
    49,
    50,
    51,
    52,
    53,
    54,
    55,
    0,
    0,
    1,
    1,
    2,
    2,
    3,
    3,
    4,
    4,
    5,
    5,
    6,
    6,
    7,
    7,
    8,
    8,
    9,
    9,
    10,
    10,
    11,
    11,
    12,
    12,
    13,
    13,
    14,
    14,
    15,
    15,
    16,
    16,
    17,
    17,
    18,
    18,
    19,
    19,
    20,
    20,
    21,
    21,
    22,
    22,
    23,
    23,
    24,
    24,
    25,
    25,
    26,
    26,
    27,
    27,
    28,
    28,
    29,
    29,
    30,
    30,
    31,
    31,
    32,
    32,
    33,
    33,
    34,
    34,
    35,
    35,
    36,
    36,
    37,
    37,
    38,
    38,
    39,
    39,
    40,
    40,
    41,
    41,
    42,
    42,
    43,
    43,
    44,
    44,
    45,
    45,
    46,
    46,
    47,
    47,
    48,
    48,
    49,
    49,
    50,
    50,
    51,
    51,
    52,
    52,
    53,
    53,
    54,
    54,
    55,
    55;
   }

   MeshMaterialList {
    1;
    84;
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0,
    0;

    Material material_1 {
     1.000000;1.000000;1.000000;1.000000;;
     11.313700;
     1.000000;1.000000;1.000000;;
     0.000000;0.000000;0.000000;;

     TextureFilename {
      "dirtymetal.jpg";
     }
    }
   }

   XSkinMeshHeader {
    1;
    1;
    1;
   }

   SkinWeights {
    "Mesh1";
    168;
    0,
    1,
    2,
    3,
    4,
    5,
    6,
    7,
    8,
    9,
    10,
    11,
    12,
    13,
    14,
    15,
    16,
    17,
    18,
    19,
    20,
    21,
    22,
    23,
    24,
    25,
    26,
    27,
    28,
    29,
    30,
    31,
    32,
    33,
    34,
    35,
    36,
    37,
    38,
    39,
    40,
    41,
    42,
    43,
    44,
    45,
    46,
    47,
    48,
    49,
    50,
    51,
    52,
    53,
    54,
    55,
    56,
    57,
    58,
    59,
    60,
    61,
    62,
    63,
    64,
    65,
    66,
    67,
    68,
    69,
    70,
    71,
    72,
    73,
    74,
    75,
    76,
    77,
    78,
    79,
    80,
    81,
    82,
    83,
    84,
    85,
    86,
    87,
    88,
    89,
    90,
    91,
    92,
    93,
    94,
    95,
    96,
    97,
    98,
    99,
    100,
    101,
    102,
    103,
    104,
    105,
    106,
    107,
    108,
    109,
    110,
    111,
    112,
    113,
    114,
    115,
    116,
    117,
    118,
    119,
    120,
    121,
    122,
    123,
    124,
    125,
    126,
    127,
    128,
    129,
    130,
    131,
    132,
    133,
    134,
    135,
    136,
    137,
    138,
    139,
    140,
    141,
    142,
    143,
    144,
    145,
    146,
    147,
    148,
    149,
    150,
    151,
    152,
    153,
    154,
    155,
    156,
    157,
    158,
    159,
    160,
    161,
    162,
    163,
    164,
    165,
    166,
    167;
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000,
    1.000000;
    1.000000,0.000000,0.000000,0.000000,0.000000,1.000000,0.000000,0.000000,0.000000,0.000000,1.000000,0.000000,0.000000,0.000000,0.000000,1.000000;;
   }
  }
 }
}