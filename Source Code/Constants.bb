;[OPTIONS]

Const OptionFile$ = "Data\options.ini"

;[ERRORS]

Const ErrorDir$ = "Logs\"

;[VERSIONS]

Const VersionNumber$ = "1.0.0"
Const CompatibleNumber$ = "1.0.0" ;Lowest version with compatible saves

;[FOREST]

Const GridSize% = 10
Const Deviation_Chance% = 40 ;out of 100
Const Branch_Chance% = 65
Const Branch_Max_Life% = 4
Const Branch_Die_Chance% = 18
Const Max_Deviation_Distance% = 3
Const Return_Chance% = 27
Const Center% = 5 ;(GridSize - 1) / 2

;[MAP]

Const MaxRoomLights% = 32
Const MaxRoomEmitters% = 8
Const MaxRoomObjects% = 30

Const ROOM1% = 1
Const ROOM2% = 2
Const ROOM2C% = 3
Const ROOM3% = 4
Const ROOM4% = 5

Const ZONE_AMOUNT% = 3

Const RoomScale# = 8.0 / 2048.0
Const MapWidth% = 12, MapHeight% = 12

;[MT]

Const GridSZ% = 19

;[CHUNKS]

Const ChunkMaxDistance# = 40.0 * 3.0

;[FLU-LIGHT]

Const MaxFluTextures% = 3
Const FluState_OFF% = 0
Const FluState_Between% = 1
Const FluState_ON% = 2
Const MaxFluSounds% = 7
Const FLU_STATE_OFF% = 0
Const FLU_STATE_ON% = 1
Const FLU_STATE_FLICKER% = 2

;[COLLISIONS]

Const HIT_MAP% = 1
Const HIT_PLAYER% = 2
Const HIT_ITEM% = 3
Const HIT_APACHE% = 4
Const HIT_178% = 5
Const HIT_DEAD% = 6

;[FPS]

Const TICK_DURATION# = 70.0 / 60.0

;[INVENTORY]

Const INVENTORY_GFX_SIZE% = 70
Const INVENTORY_GFX_SPACING% = 35

Const NAV_WIDTH% = 287
Const NAV_HEIGHT% = 256

;[ACHIEVEMENTS]

Const MAXACHIEVEMENTS% = 37

Const Achv008% = 0, Achv012% = 1, Achv035% = 2, Achv049% = 3, Achv055% = 4, Achv079% = 5, Achv096% = 6, Achv106% = 7, Achv148% = 8, Achv178% = 9
Const Achv205% = 10, Achv294% = 11, Achv372% = 12, Achv420% = 13, Achv427% = 14, Achv500% = 15, Achv513% = 16, Achv714% = 17, Achv789% = 18
Const Achv860% = 19, Achv895% = 20, Achv914% = 21, Achv939% = 22, Achv966% = 23, Achv970% = 24, Achv1025% = 25, Achv1048% = 26, Achv1123% = 27

Const AchvMaynard% = 28, AchvHarp% = 29, AchvSNAV% = 30, AchvOmni% = 31, AchvConsole% = 32, AchvTesla% = 33, AchvPD% = 34

Const Achv1162% = 35, Achv1499% = 36

Const AchvKeter% = 37

;[DIFFICULTIES]

Const SAFE% = 0
Const EUCLID% = 1
Const KETER% = 2
Const APOLLYON% = 3
Const CUSTOM% = 4

Const SAVEANYWHERE% = 0
Const SAVEONQUIT% = 1
Const SAVEONSCREENS% = 2

Const EASY% = 0
Const NORMAL% = 1
Const HARD% = 2
Const EXTREME% = 3

;[LAUNCHER]

Const L_WIDTH% = 640
Const L_HEIGHT% = 480

;[NPCS]

Const NPCtype173% = 1, NPCtypeOldMan% = 2, NPCtypeGuard% = 3, NPCtypeD% = 4
Const NPCtype372% = 6, NPCtypeApache% = 7, NPCtypeMTF% = 8, NPCtype096% = 9
Const NPCtype049% = 10, NPCtypeZombie% = 11, NPCtype5131% = 12, NPCtypeTentacle% = 13
Const NPCtype860% = 14, NPCtype939% = 15, NPCtype066% = 16, NPCtypePdPlane% = 17
Const NPCtype966% = 18, NPCtype1048a% = 19, NPCtype1499% = 20, NPCtype008% = 21, NPCtypeClerk% = 22
Const NPCtype178% = 23, NPCtype682% = 24

;[SCP-914]

Const ROUGH% = -2
Const COARSE% = -1
Const ONETOONE% = 0
Const FINE% = 1
Const VERY_FINE% = 2

;~IDEal Editor Parameters:
;~C#Blitz3D