# language: fr
Fonctionnalité: Gestion des commandes
  En tant que gestionnaire
  Je veux gérer les commandes
  Afin de suivre les achats des clients

  Scénario: Créer une nouvelle commande
    Étant donné un utilisateur "Ahmed" existe
    Et un produit "Laptop" existe
    Quand je crée une commande pour l'utilisateur "Ahmed" avec le produit "Laptop"
    Alors la commande existe dans la base avec le statut "En_attente"

  Scénario: Lister toutes les commandes
    Étant donné les commandes suivantes existent:
      | utilisateur | produit | statut     | prixTotal |
      | Ahmed       | Laptop  | En_attente | 1500.0    |
      | Fatma       | Phone   | En_cours   | 800.0     |
    Quand je demande la liste des commandes
    Alors je reçois 2 commandes

  Scénario: Mettre à jour le statut d'une commande
    Étant donné une commande existe avec le statut "En_attente"
    Quand je mets à jour la commande avec le statut "En_cours"
    Alors la commande a le statut "En_cours"

  Scénario: Supprimer une commande
    Étant donné une commande existe
    Quand je supprime la commande
    Alors la commande n'existe plus
