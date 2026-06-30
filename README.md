# Tinkers Reborn

    Make Tinker Great again

```mermaid
flowchart TB
  subgraph Interface
    ToolCore
    WeaponCore
    IToolPart
  end

  subgraph MultiBlock
    Smeltery
    LargeFurnace
  end

  subgraph Item
    subgraph ToolPart
      Shard
      ToolRod
      PickaxeHead
      ShovelHead
      AxeHead
      SwordBlade
      WideGuard
      HandGuard
      CrossBar
      ToolBinding
      Pan
      WideBoard
      KnifeBlade
      ChiselHead
      ToughRod
      ToughBinding
      LargePlate
      BroadaxeHead
      ScytheHead
      ExcavatorHead
      LargeBlade
      HammerHead
      FullGuard
      BowSting
      Fletching
      ArrowHead
      Shaft
      ShurikenPart
      CrossBowLimb
      CrossBowBody
      BowLimb
      BoltCore
    end
    
    subgraph Tool
      Pickaxe
      Hatchet
      Shovel
      Mattock
      Chisel
      LumberAxe
      Excavator
      Hammer

      subgraph Weapon
        BroadSword
        LongSword
        Rapier
        FryingPan
        BattleSign
        Dagger
        Scythe
        Cleaver
        BattleAxe
        Shuriken
        ThrowingKnife
        Javelin
        ShortBow
        LongBow
        CrossBow
        Cutlass
        Arrow
        Bolt
      end

    end
  end

  subgraph Block
    subgraph GeneralBlock
      SlimeGrass
      SlimeDirt
      SlimeSoil
      SlimyMud
      Grout
      GraveyardSoil
      ConsecratedSoil
      SearedBricks
    end

    subgraph WorkingBlock
      ToolStation
      ToolForge
      PartBuilder
      PatternChest
      PartChest
      StencilTable
      CraftingStation
      FurnaceController
      SmelteryController
      SmelteryDrain
      SearedTank
      SearedGlass
      SearedWindow
      CastingTable
      SearedFaucet
      CastingBasin
      CastingChannel
    end

  end

  subgraph WorldGen
    Village
    SlimeIsland
    Ore
  end

  
  Pickaxe --> ToolCore
  Pickaxe --> PickaxeHead
  Pickaxe --> ToolBinding
  Pickaxe --> ToolRod

  Hatchet --> ToolCore
  Hatchet --> AxeHead
  Hatchet --> ToolRod
  
  Shovel --> ToolCore
  Shovel --> ShovelHead
  Shovel --> ToolRod

  Mattock --> ToolCore
  Mattock --> ShovelHead
  Mattock --> AxeHead
  Mattock --> ToolRod

  Chisel --> ToolCore
  Chisel --> ChiselHead
  Chisel --> ToolRod

  LumberAxe --> ToolCore
  LumberAxe --> BroadaxeHead
  LumberAxe --> LargePlate
  LumberAxe --> ToughRod
  LumberAxe --> ToughBinding

  Excavator --> ToolCore
  Excavator --> ExcavatorHead
  Excavator --> LargePlate
  Excavator --> ToughRod
  Excavator --> ToughBinding

  Hammer --> ToolCore
  Hammer --> HammerHead
  Hammer --> LargePlate
  Hammer --> ToughRod
  Hammer --> ToughBinding

  Scythe --> WeaponCore
  Scythe --> ScytheHead
  Scythe --> LargePlate
  Scythe --> ToughRod
  Scythe --> ToughBinding

  WeaponCore --> ToolCore

  BroadSword --> WeaponCore
  BroadSword --> SwordBlade
  BroadSword --> WideGuard
  BroadSword --> ToolRod

  LongSword --> WeaponCore
  LongSword --> SwordBlade
  LongSword --> HandGuard
  LongSword --> ToolRod

  Rapier --> WeaponCore
  Rapier --> SwordBlade
  Rapier --> CrossBar
  Rapier --> ToolRod

  FryingPan --> WeaponCore
  FryingPan --> Pan
  FryingPan --> ToolRod

  BattleSign --> WeaponCore
  BattleSign --> WideBoard
  BattleSign --> ToolRod

  Dagger --> WeaponCore
  Dagger --> KnifeBlade
  Dagger --> CrossBar
  Dagger --> ToolRod

  Cleaver --> WeaponCore
  Cleaver --> LargeBlade
  Cleaver --> LargePlate
  Cleaver --> ToughRod

  BattleAxe --> WeaponCore
  BattleAxe --> BroadaxeHead
  BattleAxe --> ToughRod
  BattleAxe --> ToughBinding

  Shuriken --> WeaponCore
  Shuriken --> ShurikenPart

  ThrowingKnife --> WeaponCore
  ThrowingKnife --> KnifeBlade
  ThrowingKnife --> ToolRod

  Javelin --> WeaponCore
  Javelin --> ArrowHead
  Javelin --> ToughRod

  ShortBow --> WeaponCore
  ShortBow --> BowLimb
  ShortBow --> BowSting

  LongBow --> WeaponCore
  LongBow --> BowLimb
  LongBow --> BowSting
  LongBow --> LargePlate

  CrossBow --> WeaponCore
  CrossBow --> CrossBowLimb
  CrossBow --> CrossBowBody
  CrossBow --> BowSting
  CrossBow --> ToughBinding

  Cutlass --> WeaponCore
  Cutlass --> SwordBlade
  Cutlass --> FullGuard
  Cutlass --> ToolRod

  Arrow --> WeaponCore
  Arrow --> ArrowHead
  Arrow --> Shaft
  Arrow --> Fletching

  Bolt --> WeaponCore
  Bolt --> BoltCore
  Bolt --> Fletching

  SmelteryController --> Smeltery
  SmelteryDrain --> Smeltery
  SearedTank --> Smeltery
  SearedGlass --> Smeltery
  SearedWindow --> Smeltery
  CastingTable --> Smeltery
  SearedFaucet --> Smeltery
  CastingBasin --> Smeltery
  CastingChannel --> Smeltery
  SearedBricks --> Smeltery

  FurnaceController --> LargeFurnace
  SearedTank --> LargeFurnace

  Smeltery --> Village

  SlimeGrass --> SlimeIsland
  SlimeDirt --> SlimeIsland
  SlimeSoil --> SlimeIsland
  SlimyMud --> SlimeIsland

```

## NBT

```json

"TinkersRebornTool":{
  // actual render materials identifier in order
  "RenderMaterials":["wood", "wood", "obsidian"],
  // materials identifier in order READONLY
  "Materials":["iron", "wood", "obsidian"],
  // TODO
  "Modifiers":[
    {
      "identifier":"haste",
      "color":9502720,
      "type":"modify"
    },
    {
      "identifier":"ecological",
      "color":-7444965,
      "type":"trait"
    }
  ],
  // this tool's now stats
  "Stats":{
    "Durability": 1,
    "Attack": 2.1,
    "MiningSpeed": 2.2,
    "HarvestLevel": 3,
    "ModifierSlots": 3, // this never change
    "ExtraModifiers": 10, // this add by modifier/trait/level up
    "UsedModifiers": 0
  },
  // this tool's base stats READONLY
  "StatsOriginal":{
    "Durability": 0.5,
    "Attack": 2.1,
    "MiningSpeed": 1.0,
    "HarvestLevel": 3,
    "ModifierSlots": 3,
    "ExtraModifiers": 0,
    "UsedModifiers": 0
  },
  // for some traits data
  "Special":{
    "alien":{
      "pool":{
        "durability":282,
        "attack":1.349999,
        "speed":1.7359989
      },
      "bonus":{
        "durability":19,
        "attack":0.110000014,
        "speed":0.14
      }
    }
  },
  "CategoryList": ["harvest", "tool"],
  // 1 broken, 0 not broken, use boolean
  "Broken":0,
  "Unbreakable":1,
  "RepairCount":10
}

```

## TODO

- [ ] furnace
- [ ] smeltery
- [ ] harvest level function
