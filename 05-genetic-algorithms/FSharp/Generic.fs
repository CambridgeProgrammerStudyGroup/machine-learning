module Generic

open System

let ga population select breed crossover observe terminate  = 
    let popSize = Array.length population
    let rec generate population generation nextUpdateTime =        
        let newPop =
            population
                |> select
                |> breed popSize crossover 
        
        observe newPop generation
        if terminate newPop generation then
            newPop
        else
            generate newPop (generation + 1) nextUpdateTime
    generate population 0 DateTime.MinValue


let select population =
    population
        |> Array.sortBy (snd)
        |> Array.skip (population.Length / 2)


let breed getMutationRate targetSize crossover population = [| 
    // will blow up of popSize < 2
    yield! population
    let popSize = Array.length population
    let random = new Random()
    let pickPoint () = Math.Pow(random.NextDouble(), 1.4)
    let pickFirst () = int (pickPoint () * float popSize)
    let pickSecond first =
        let pick = int (pickPoint () * float (popSize - 1))
        if pick < first then pick else pick + 1    
    let mutationRate = getMutationRate population
    for _ in [1..(targetSize - popSize)] do
        let first = pickFirst ()
        let second = pickSecond first
        yield crossover mutationRate population.[first] population.[second] |]