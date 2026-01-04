# language: fr
Fonctionnalité: Gestion des produits
  En tant qu'administrateur
  Je veux gérer les produits
  Afin de maintenir le catalogue de produits

  Scénario: Créer un nouveau produit
    Étant donné la base de données des produits est vide
    Quand je crée un produit "Laptop" avec le prix "1500.0" et quantité "10"
    Alors le produit "Laptop" existe dans la base

  Scénario: Lister tous les produits
    Étant donné les produits suivants existent:
      | nameProduct | price | quantity |
      | Laptop      | 1500.0| 10       |
      | Phone       | 800.0 | 20       |
    Quand je demande la liste des produits
    Alors je reçois 2 produits

  Scénario: Mettre à jour un produit
    Étant donné un produit "Laptop" existe avec l'ID 1
    Quand je mets à jour le produit 1 avec le nom "Laptop Pro" et prix "2000.0"
    Alors le produit 1 a le nom "Laptop Pro" et prix "2000.0"

  Scénario: Supprimer un produit
    Étant donné un produit "Laptop" existe avec l'ID 1
    Quand je supprime le produit avec l'ID 1
    Alors le produit avec l'ID 1 n'existe plus