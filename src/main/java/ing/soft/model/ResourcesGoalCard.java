package ing.soft.model;

public class ResourcesGoalCard extends GoalCard{
    private final Resource[] resources;
    private final Playerboard board;

    public ResourcesGoalCard(int points, Resource[] resources, Playerboard board) {
        super(points);
        this.resources = resources;
        this.board = board;
    }

    //I think we should add a reference to the playerboard to check the goal
    public int checkGoal() {
        int pointsScored = 1000; /*I initialized pointsScored to 1000 because in
            the final check I always take the minimum value*/
        int wolves = 0, mushrooms = 0, leaves = 0, butterflies = 0, feathers = 0, glassvials = 0, scrolls = 0;

        /*Here I check the resources array, to understand what resources we are looking for
        and how many are there*/

        for (Resource value : resources) {
            switch (value) {
                case WOLF -> wolves++;
                case MUSHROOM -> mushrooms++;
                case LEAF -> leaves++;
                case BUTTERFLY -> butterflies++;
                case FEATHER -> feathers++;
                case SCROLL -> scrolls++;
                case GLASSVIAL -> glassvials++;
            }
        }

        int countwolves = 0, countmushrooms = 0, countleaves = 0, countbutterflies = 0, countfeathers = 0, countglassvials = 0, countscrolls = 0;

        /*Now I compare the resources in the resources array and check how many of them
        are there in the board*/

        for (int i = 0; i < board.getResources.length; i++) {
            switch (board.getResources[i]) {
                case WOLF -> {
                    if (wolves == 0) {
                        break;
                    } else {
                        countwolves++;
                    }
                }

                case MUSHROOM -> {
                    if (mushrooms == 0) {
                        break;
                    } else {
                        countmushrooms++;
                    }
                }

                case LEAF -> {
                    if (leaves == 0) {
                        break;
                    } else {
                        countleaves++;
                    }
                }

                case BUTTERFLY -> {
                    if (butterflies == 0) {
                        break;
                    } else {
                        countbutterflies++;
                    }
                }

                case FEATHER -> {
                    if (feathers == 0) {
                        break;
                    } else {
                        countfeathers++;
                    }
                }

                case GLASSVIAL -> {
                    if (glassvials == 0) {
                        break;
                    } else {
                        countglassvials++;
                    }
                }

                case SCROLL -> {
                    if (scrolls == 0) {
                        break;
                    } else {
                        countscrolls++;
                    }
                }
            }
        }

        /*Now I have to count the points*/

        for (Resource resource : resources) {
            int partialScore = 0;
            switch (resource) {
                case WOLF -> {
                    if (countwolves == 0) {
                        break;
                    } else {
                        partialScore = countwolves / wolves;
                        if (partialScore < pointsScored) {
                            pointsScored = partialScore;
                        }
                    }

                }
                case MUSHROOM -> {
                    if (countmushrooms == 0) {
                        break;
                    } else {
                        partialScore = countmushrooms / mushrooms;
                        if (partialScore < pointsScored) {
                            pointsScored = partialScore;
                        }
                    }
                }
                case LEAF -> {
                    if (countleaves == 0) {
                        break;
                    } else {
                        partialScore = countleaves / leaves;
                        if (partialScore < pointsScored) {
                            pointsScored = partialScore;
                        }
                    }
                }

                case BUTTERFLY -> {
                    if (countbutterflies == 0) {
                        break;
                    } else {
                        partialScore = countbutterflies / butterflies;
                        if (partialScore < pointsScored) {
                            pointsScored = partialScore;
                        }
                    }
                }
                case FEATHER -> {
                    if (countfeathers == 0) {
                        break;
                    } else {
                        partialScore = countfeathers / feathers;
                        if (partialScore < pointsScored) {
                            pointsScored = partialScore;
                        }
                    }
                }
                case SCROLL -> {
                    if (countscrolls == 0) {
                        break;
                    } else {
                        partialScore = countscrolls / scrolls;
                        if (partialScore < pointsScored) {
                            pointsScored = partialScore;
                        }
                    }
                }
                case GLASSVIAL -> {
                    if (countglassvials == 0) {
                        break;
                    } else {
                        partialScore = countglassvials / glassvials;
                        if (partialScore < pointsScored) {
                            pointsScored = partialScore;
                        }
                    }
                }
            }
        }

        if(pointsScored == 1000){
            pointsScored = 0;
            return pointsScored;
        }else{return pointsScored;}
    }
}
