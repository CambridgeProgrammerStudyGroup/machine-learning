open System
open Generic
open ArrayPop
open Data

(* 
    Fixed length string mutation to create Hamelet's soliloquy.

    Uses "Uniform" crossover which is probably why the smaller 
    the population the better the efficiency

*)
    
[<EntryPoint>]
let main argv = 

    let popSize = 3 
    let text =  toBeOrNotToBe

    let target = text.ToCharArray();
    let fitness = distance (target)  
    
    let chars =  [|0..127|] |> Array.map char
    let chars = Array.distinct target
    let createPeep () = getRandomSelection target.Length chars

    let population =
        [|1..popSize|]
        |> Array.map (fun i ->
            let peep = createPeep()
            (peep, fitness peep))

    let getMutationRate population = 0.5
    let breedPeep = breed getMutationRate
    
    let crossoverPeep = crossover createPeep fitness

    let sw = new System.Diagnostics.Stopwatch()
    sw.Start()
    let display bestPeep bestFit generation =  
        let bestText = new String(bestPeep : char[])
        printfn "%s \n\n>completed generation %i %s %f" bestText generation (sw.Elapsed.ToString(@"hh\:mm\:ss\.fff")) bestFit
    
    let mutable nextUpdateTime = DateTime.UtcNow
    let observe pop generation = 
        if DateTime.UtcNow > nextUpdateTime then
            let best = Array.head pop
            let bestPeep, bestFit = best
            display bestPeep bestFit generation
            nextUpdateTime <- DateTime.UtcNow.AddSeconds(1.0)

    let terminate population generation = 
        let best = Array.head population
        let bestPeep, bestFit = best
        
        if bestFit < 1.0 then
            false
        else
            display bestPeep bestFit generation
            printfn "Done!" 
            Console.ReadKey() |> ignore
            true

    let eugenes = ga population select breedPeep crossoverPeep observe terminate 

    printfn "%A" argv
    0 
