
#Input handling
@@@ Consume button presses. Key events are batched and then dispatched in a single event. Mark specific keys as consumed.
@@@ Explain isJustPressed
@@@ Explain isPressed with/without consumed keys

##Key config

@@@ maps physical keys, joypad buttons, mouse buttons to logical functions -- textContinue, cancel, etc.
@@@ Logical functions available through VKey

##Joypad support
@@@ Not implemented yet. TODO: Create issue and mention issue number here

##Touch and mouse support

@@@ Explain how touch/mouse map to 'pointer' position.

@@@ isTouchScreen() is currently only available from the IRenderEnv. Shouldn't it also be available from IInput? It determines how the pointer stuff works after all.
@@@ Fix Button implementation so it won't show rollover for touchscreen devices.

###Mouse wheel

@@@ Explain how mouse wheel is integrated

##Idle

@@@ Used to determine play time. No input -- doesn't count.

