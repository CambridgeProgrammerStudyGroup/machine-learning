module Primes

open System
open System.IO
open System.Text.RegularExpressions

open Persistence

let private primes =
    dataDir  + "\TheFirst10,000Primes.txt"
    |> File.ReadAllLines
    |> Array.filter  (fun l ->  Regex.IsMatch(l, @"^\s*\d"))
    |> Array.collect  (fun l -> Regex.Split(l, @"\s+", RegexOptions.Singleline))
    |> Array.filter (fun t -> not (String.IsNullOrEmpty t))
    |> Array.map (fun t -> int t)

let getPrimes greaterThan lessThan =
    primes |> Array.filter(fun p -> greaterThan < p && p < lessThan)
    